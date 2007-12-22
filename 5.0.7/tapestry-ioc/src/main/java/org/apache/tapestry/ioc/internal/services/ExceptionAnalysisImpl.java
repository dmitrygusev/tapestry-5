// Copyright 2006 The Apache Software Foundation
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

package org.apache.tapestry.ioc.internal.services;

import org.apache.tapestry.ioc.services.ExceptionAnalysis;
import org.apache.tapestry.ioc.services.ExceptionInfo;

import static java.util.Collections.unmodifiableList;
import java.util.List;

/**
 *
 */
public class ExceptionAnalysisImpl implements ExceptionAnalysis
{
    private final List<ExceptionInfo> _infos;

    public ExceptionAnalysisImpl(final List<ExceptionInfo> infos)
    {
        _infos = unmodifiableList(infos);
    }

    public List<ExceptionInfo> getExceptionInfos()
    {
        return _infos;
    }

    @Override
    public String toString()
    {
        ExceptionInfo first = _infos.get(0);

        return String.format("ExceptionAnalysis[%s -- %s]", first.getClassName(), first
                .getMessage());
    }
}
