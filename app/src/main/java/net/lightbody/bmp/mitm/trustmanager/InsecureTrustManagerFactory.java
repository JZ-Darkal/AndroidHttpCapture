/*
 * Copyright 2014 The Netty Project,
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package net.lightbody.bmp.mitm.trustmanager;

import java.security.KeyStore;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import cn.darkal.networkdiagnosis.Utils.AbstractX509ExtendedTrustManager;
import io.netty.handler.ssl.util.SimpleTrustManagerFactory;

/**
 * <b>Note:</b> This is a modified version of {@link io.netty.handler.ssl.util.InsecureTrustManagerFactory} from Netty
 * 4.0.36. Unlike the netty version, this class returns an {@link AbstractX509ExtendedTrustManager} instead of an
 * {@link javax.net.ssl.X509TrustManager} instance, which allows us to bypass additional certificate validations.
 * <p/>
 * An insecure {@link TrustManagerFactory} that trusts all X.509 certificates without any verification.
 * <p>
 * <strong>NOTE:</strong>
 * Never use this {@link TrustManagerFactory} in production.
 * It is purely for testing purposes, and thus it is very insecure.
 * </p>
 */
public class InsecureTrustManagerFactory extends SimpleTrustManagerFactory {

    public static final TrustManagerFactory INSTANCE = new InsecureTrustManagerFactory();

    public static final AbstractX509ExtendedTrustManager tm = new InsecureExtendedTrustManager();

    @Override
    protected void engineInit(KeyStore keyStore) throws Exception {
    }

    @Override
    protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws Exception {
    }

    @Override
    protected TrustManager[] engineGetTrustManagers() {
        return new TrustManager[]{tm};
    }
}
