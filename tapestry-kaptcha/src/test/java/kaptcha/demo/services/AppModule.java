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

package kaptcha.demo.services;

import com.google.code.kaptcha.Constants;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.services.ApplicationDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.kaptcha.services.KaptchaModule;
import org.apache.tapestry5.kaptcha.services.KaptchaProducer;
import org.slf4j.Logger;

import java.awt.image.BufferedImage;

@SubModule(KaptchaModule.class)
public class AppModule
{
    @Contribute(SymbolProvider.class)
    @ApplicationDefaults
    public static void provideApplicationDefaults(
            MappedConfiguration<String, String> configuration)
    {
        configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
    }

    public static Object decorateKaptchaProducer(final KaptchaProducer producer, final Logger logger)
    {
        return new KaptchaProducer()
        {

            public int getWidth()
            {
                return producer.getWidth();
            }

            public int getHeight()
            {
                return producer.getHeight();
            }

            public BufferedImage createImage(String text)
            {
                return producer.createImage(text);
            }

            public String createText()
            {
                logger.info(String.format("Kaptcha text: '%s'", producer.createText()));

                return "i8cookies";
            }
        };
    }

    @Contribute(KaptchaProducer.class)
    public static void configureKaptchaProducer(MappedConfiguration<String, String> configuration)
    {
        configuration.add(Constants.KAPTCHA_IMAGE_WIDTH, "210");
    }
}
