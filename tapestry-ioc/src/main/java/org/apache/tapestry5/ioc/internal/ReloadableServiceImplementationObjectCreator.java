// Copyright 2010 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.ioc.internal;

import java.lang.reflect.Constructor;

import org.apache.tapestry5.ioc.ObjectCreator;
import org.apache.tapestry5.ioc.ServiceBuilderResources;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;

/**
 * Returns an {@link ObjectCreator} for lazily instantiation a given implementation class (with dependencies).
 * Once an instance is instantiated, it is cached ... until the underlying .class file changes, at which point
 * the class is reloaded and a new instance instantiated.
 */
public class ReloadableServiceImplementationObjectCreator extends AbstractReloadableObjectCreator
{
    private final ServiceBuilderResources resources;

    public ReloadableServiceImplementationObjectCreator(ServiceBuilderResources resources, ClassLoader baseClassLoader,
            String implementationClassName)
    {
        super(baseClassLoader, implementationClassName, resources.getLogger(), resources.getTracker());

        this.resources = resources;
    }

    protected Object createInstance(Class clazz)
    {
        final Constructor constructor = InternalUtils.findAutobuildConstructor(clazz);

        if (constructor == null)
            throw new RuntimeException(String.format(
                    "Service implementation class %s does not have a suitable public constructor.", clazz.getName()));

        ObjectCreator constructorServiceCreator = new ConstructorServiceCreator(resources, String.format(
                "%s (last modified %tc)", constructor, getLastModifiedTimestamp()), constructor);

        return constructorServiceCreator.createObject();
    }
}