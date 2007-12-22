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

import org.apache.tapestry.ComponentResources;
import org.apache.tapestry.internal.test.InternalBaseTestCase;
import static org.apache.tapestry.ioc.internal.util.CollectionFactory.newMap;
import org.apache.tapestry.model.ComponentModel;
import org.apache.tapestry.services.MetaDataLocator;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Map;

public class MetaDataLocatorImplTest extends InternalBaseTestCase
{
    @Test
    public void found_in_component()
    {
        ComponentResources resources = mockComponentResources();
        ComponentModel model = mockComponentModel();

        String key = "foo.bar";
        String value = "zaphod";
        String completeId = "foo.Bar:baz";

        train_getCompleteId(resources, completeId);
        train_getComponentModel(resources, model);
        train_getMeta(model, key, value);

        replay();

        Map<String, String> configuration = Collections.emptyMap();

        MetaDataLocator locator = new MetaDataLocatorImpl(configuration);

        assertSame(locator.findMeta(key, resources), value);

        verify();

        // And check that it's cached:

        train_getCompleteId(resources, completeId);

        replay();

        assertSame(locator.findMeta(key, resources), value);

        verify();
    }

    @Test
    public void found_in_container()
    {
        ComponentResources resources = mockComponentResources();
        ComponentResources containerResources = mockComponentResources();
        ComponentModel model = mockComponentModel();
        ComponentModel containerModel = mockComponentModel();

        String key = "foo.bar";
        String value = "zaphod";
        String completeId = "foo.Bar:baz";

        train_getCompleteId(resources, completeId);
        train_getComponentModel(resources, model);
        train_getMeta(model, key, null);
        train_getContainerResources(resources, containerResources);
        train_getComponentModel(containerResources, containerModel);
        train_getMeta(containerModel, key, value);

        replay();

        Map<String, String> configuration = Collections.emptyMap();

        MetaDataLocator locator = new MetaDataLocatorImpl(configuration);

        assertSame(locator.findMeta(key, resources), value);

        verify();
    }

    @Test
    public void found_via_high_level_default()
    {
        ComponentResources resources = mockComponentResources();
        ComponentModel model = mockComponentModel();

        String key = "foo.bar";
        String value = "zaphod";
        String completeId = "Bar";
        String logicalPageName = completeId;

        train_getCompleteId(resources, completeId);
        train_getComponentModel(resources, model);
        train_getMeta(model, key, null);
        train_getContainerResources(resources, null);

        train_getPageName(resources, logicalPageName);

        replay();

        Map<String, String> configuration = newMap();
        configuration.put(key, value);

        MetaDataLocator locator = new MetaDataLocatorImpl(configuration);

        assertSame(locator.findMeta(key, resources), value);

        verify();

        // And check that it's cached:

        train_getCompleteId(resources, completeId);

        replay();

        assertSame(locator.findMeta(key, resources), value);

        verify();
    }

    @Test
    public void default_matching_is_case_insensitive()
    {
        ComponentResources resources = mockComponentResources();
        ComponentModel model = mockComponentModel();

        String key = "foo.bar";
        String value = "zaphod";
        String completeId = "foo.Bar";

        train_getCompleteId(resources, completeId);
        train_getComponentModel(resources, model);
        train_getMeta(model, key, null);
        train_getContainerResources(resources, null);

        train_getPageName(resources, "foo/Bar");

        replay();

        Map<String, String> configuration = newMap();
        configuration.put(key.toUpperCase(), value);

        MetaDataLocator locator = new MetaDataLocatorImpl(configuration);

        assertSame(locator.findMeta(key, resources), value);

        verify();

        // And check that it's cached:

        train_getCompleteId(resources, completeId);

        replay();

        assertSame(locator.findMeta(key, resources), value);

        verify();
    }

    @Test
    public void subfolder_default_overrides_high_level_default()
    {
        ComponentResources resources = mockComponentResources();
        ComponentModel model = mockComponentModel();

        String key = "foo.bar";
        String value = "zaphod";
        String completeId = "foo.Bar";

        train_getCompleteId(resources, completeId);
        train_getComponentModel(resources, model);
        train_getMeta(model, key, null);
        train_getContainerResources(resources, null);

        train_getPageName(resources, "foo/Bar");

        replay();

        Map<String, String> configuration = newMap();
        configuration.put(key, "xxx");
        configuration.put("foo:" + key, value);

        MetaDataLocator locator = new MetaDataLocatorImpl(configuration);

        assertSame(locator.findMeta(key, resources), value);

        verify();

        // And check that it's cached:

        train_getCompleteId(resources, completeId);

        replay();

        assertSame(locator.findMeta(key, resources), value);

        verify();
    }

    @Test
    public void test_cache_cleared()
    {
        ComponentResources resources = mockComponentResources();
        ComponentModel model = mockComponentModel();

        String key = "foo.bar";
        String value = "zaphod";
        String completeId = "foo.Bar:baz";

        train_getCompleteId(resources, completeId);
        train_getComponentModel(resources, model);
        train_getMeta(model, key, value);

        replay();

        Map<String, String> configuration = Collections.emptyMap();

        MetaDataLocatorImpl locator = new MetaDataLocatorImpl(configuration);

        assertSame(locator.findMeta(key, resources), value);

        verify();

        // And check that it's cached:

        train_getCompleteId(resources, completeId);
        train_getComponentModel(resources, model);
        train_getMeta(model, key, value);

        replay();

        locator.objectWasInvalidated();

        assertSame(locator.findMeta(key, resources), value);

        verify();
    }
}
