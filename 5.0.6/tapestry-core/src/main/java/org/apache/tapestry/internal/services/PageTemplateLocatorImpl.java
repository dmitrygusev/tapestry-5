// Copyright 2007 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry.internal.services;

import static java.lang.String.format;

import java.util.Locale;

import org.apache.tapestry.internal.InternalConstants;
import org.apache.tapestry.ioc.Resource;
import org.apache.tapestry.ioc.internal.util.InternalUtils;
import org.apache.tapestry.model.ComponentModel;
import org.apache.tapestry.services.ComponentClassResolver;

public class PageTemplateLocatorImpl implements PageTemplateLocator
{
    private final Resource _contextRoot;

    private final ComponentClassResolver _resolver;

    public PageTemplateLocatorImpl(Resource contextRoot, ComponentClassResolver resolver)
    {
        _contextRoot = contextRoot;
        _resolver = resolver;
    }

    public Resource findPageTemplateResource(ComponentModel model, Locale locale)
    {
        String className = model.getComponentClassName();

        // A bit of a hack, but should work.

        if (!className.contains(".pages.")) return null;

        String logicalName = _resolver.resolvePageClassNameToPageName(className);

        int slashx = logicalName.lastIndexOf('/');

        if (slashx > 0)
        {
            // However, the logical name isn't quite what we want. It may have been somewhat
            // trimmed.

            String simpleClassName = InternalUtils.lastTerm(className);

            logicalName = logicalName.substring(0, slashx + 1) + simpleClassName;
        }

        String path = format("%s.%s", logicalName, InternalConstants.TEMPLATE_EXTENSION);

        return _contextRoot.forFile(path).forLocale(locale);
    }

}
