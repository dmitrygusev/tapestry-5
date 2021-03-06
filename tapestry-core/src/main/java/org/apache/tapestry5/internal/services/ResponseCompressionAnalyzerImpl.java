// Copyright 2009, 2010, 2012 The Apache Software Foundation
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

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.TapestryInternalUtils;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.ResponseCompressionAnalyzer;

import javax.servlet.http.HttpServletRequest;

public class ResponseCompressionAnalyzerImpl implements ResponseCompressionAnalyzer
{
    private final HttpServletRequest request;

    private final boolean gzipCompressionEnabled;

    public ResponseCompressionAnalyzerImpl(HttpServletRequest request,
                                           @Symbol(SymbolConstants.GZIP_COMPRESSION_ENABLED)
                                           boolean gzipCompressionEnabled)
    {
        this.request = request;
        this.gzipCompressionEnabled = gzipCompressionEnabled;
    }

    public boolean isGZipSupported()
    {
        if (!gzipCompressionEnabled)
        {
            return false;
        }

        // TAP5-1880:
        if (request.getProtocol() == "HTTP/1.0")
        {
            return false;
        }

        String supportedEncodings = request.getHeader("Accept-Encoding");

        if (supportedEncodings == null)
        {
            return false;
        }

        for (String encoding : TapestryInternalUtils.splitAtCommas(supportedEncodings))
        {
            if (encoding.equalsIgnoreCase("gzip"))
            {
                return true;
            }
        }

        return false;
    }
}
