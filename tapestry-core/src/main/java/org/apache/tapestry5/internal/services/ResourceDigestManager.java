// Copyright 2006, 2008, 2009, 2011 The Apache Software Foundation
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

package org.apache.tapestry5.internal.services;

import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.services.InvalidationEventHub;
import org.apache.tapestry5.services.ResourceDigestGenerator;

/**
 * Caches information about resources on the classpath. In addition, acts as an invalidation hub for any resources for
 * which information is obtained (when any of the resources are changed, invalidation listeners are notified so they can
 * clear their caches).
 * <p/>
 * Note that the name and role of this class changed (and diminished) quite a bit in Tapestry 5.3. It is now focused on
 * determining which files require a digest, and which what the digests are for resources.
 *
 * @deprecated Deprecated in 5.4 with no replacement; see release notes about classpath assets moving to /META-INF/assets/.
 */
public interface ResourceDigestManager extends InvalidationEventHub
{
    /**
     * Returns true if the path requires that the client URL for the resource include a digest to validate that the
     * client is authorized to access the resource.
     *
     * @param resource
     * @return true if digest is required for the resource
     * @see ResourceDigestGenerator#requiresDigest(String)
     */
    boolean requiresDigest(Resource resource);

    /**
     * Returns the digest for the given path.
     *
     * @param resource
     * @return the digest, or null if the resource does not exist
     */
    String getDigest(Resource resource);
}
