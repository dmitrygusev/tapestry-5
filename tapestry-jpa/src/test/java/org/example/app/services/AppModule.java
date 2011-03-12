// Copyright 2011 The Apache Software Foundation
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

package org.example.app.services;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Match;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.services.ApplicationDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.jpa.EntityManagerSource;
import org.apache.tapestry5.jpa.JpaModule;
import org.apache.tapestry5.jpa.JpaTransactionAdvisor;
import org.apache.tapestry5.jpa.PersistenceUnitConfigurer;
import org.apache.tapestry5.jpa.TapestryPersistenceUnitInfo;
import org.example.app.AppConstants;
import org.example.app.entities.User;
import org.example.app.services.impl.UserDAOImpl;

@SubModule(JpaModule.class)
public class AppModule
{

    public static void bind(final ServiceBinder binder)
    {
        binder.bind(UserDAO.class, UserDAOImpl.class);
    }

    @Contribute(SymbolProvider.class)
    @ApplicationDefaults
    public static void provideApplicationDefaults(
            final MappedConfiguration<String, String> configuration)
    {
        configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
    }

    @Contribute(EntityManagerSource.class)
    public static void configurePersistenceUnitInfos(
            final MappedConfiguration<String, PersistenceUnitConfigurer> configuration)
    {
        final PersistenceUnitConfigurer configurer = new PersistenceUnitConfigurer()
        {
            public void configure(final TapestryPersistenceUnitInfo unitInfo)
            {
                unitInfo.addManagedClass(User.class);
            }
        };
        configuration.add(AppConstants.TEST_PERSISTENCE_UNIT, configurer);

    }

    @Match("*DAO")
    public static void adviseTransactionally(final JpaTransactionAdvisor advisor,
            final MethodAdviceReceiver receiver)
    {
        advisor.addTransactionCommitAdvice(receiver);
    }
}