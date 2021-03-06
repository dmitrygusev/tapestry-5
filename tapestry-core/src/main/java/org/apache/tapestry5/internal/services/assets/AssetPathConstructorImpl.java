// Copyright 2010, 2011, 2012 The Apache Software Foundation
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

package org.apache.tapestry5.internal.services.assets;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;
import org.apache.tapestry5.services.BaseURLSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.assets.AssetPathConstructor;

public class AssetPathConstructorImpl implements AssetPathConstructor
{
    private final Request request;

    private final String prefix;

    private final BaseURLSource baseURLSource;

    private final boolean fullyQualified;

    public AssetPathConstructorImpl(Request request,
                                    BaseURLSource baseURLSource,

                                    @Symbol(SymbolConstants.CONTEXT_PATH)
                                    String contextPath,

                                    @Symbol(SymbolConstants.APPLICATION_VERSION)
                                    String applicationVersion,

                                    @Symbol(SymbolConstants.APPLICATION_FOLDER)
                                    String applicationFolder,

                                    @Symbol(SymbolConstants.ASSET_URL_FULL_QUALIFIED)
                                    boolean fullyQualified,

                                    @Symbol(SymbolConstants.ASSET_PATH_PREFIX)
                                    String assetPathPrefix)
    {
        this.request = request;
        this.baseURLSource = baseURLSource;

        this.fullyQualified = fullyQualified;

        StringBuilder prefix = new StringBuilder();

        boolean needsSlash = false;

        if (contextPath.length() == 0) {
            prefix.append("/");
        }
        else {
            prefix.append(contextPath);
            needsSlash = true;
        }

        if (!applicationFolder.equals("")) {

            if (needsSlash) {
                prefix.append("/");
            }

            prefix.append(applicationFolder).append("/");

            needsSlash = false;
        }

        if (needsSlash) {
            prefix.append("/");
        }

        prefix.append(assetPathPrefix).append("/").append(applicationVersion).append("/");

        this.prefix = prefix.toString();
    }

    public String constructAssetPath(String virtualFolder, String path)
    {
        assert InternalUtils.isNonBlank(virtualFolder);
        assert path != null;

        StringBuilder builder = new StringBuilder();

        if (fullyQualified)
        {
            builder.append(baseURLSource.getBaseURL(request.isSecure()));
        }

        builder.append(prefix);
        builder.append(virtualFolder);

        if (InternalUtils.isNonBlank(path))
        {
            builder.append('/');
            builder.append(path);
        }

        return builder.toString();
    }
}
