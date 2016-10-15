package cn.darkal.networkdiagnosis.Utils;

/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.X509TrustManager;

/**
 * Allows the connection constraints such as hostname verification and algorithm
 * constraints to be checked along with the checks done in
 * {@link X509TrustManager}.
 *
 * @hide
 * @see SSLParameters#setEndpointIdentificationAlgorithm(String)
 * @since 1.7
 */
public abstract class X509ExtendedTrustManager implements X509TrustManager {
    /**
     * Checks whether the specified certificate chain (partial or complete) can
     * be validated and is trusted for client authentication for the specified
     * authentication type.
     * <p/>
     * If the {@code socket} is supplied, its {@link SSLParameters} will be
     * checked for endpoint identification.
     *
     * @param chain    the certificate chain to validate.
     * @param authType the authentication type used.
     * @param socket   the socket from which to check the {@link SSLParameters}
     * @throws CertificateException     if the certificate chain can't be validated
     *                                  or isn't trusted.
     * @throws IllegalArgumentException if the specified certificate chain is
     *                                  empty or {@code null}, or if the specified authentication
     *                                  type is {@code null} or an empty string.
     */
    public abstract void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket)
            throws CertificateException;

    /**
     * Checks whether the specified certificate chain (partial or complete) can
     * be validated and is trusted for server authentication for the specified
     * key exchange algorithm.
     * <p/>
     * If the {@code socket} is supplied, its {@link SSLParameters} will be
     * checked for endpoint identification.
     *
     * @param chain    the certificate chain to validate.
     * @param authType the authentication type used.
     * @param socket   the socket from which to check the {@link SSLParameters}
     * @throws CertificateException     if the certificate chain can't be validated
     *                                  or isn't trusted.
     * @throws IllegalArgumentException if the specified certificate chain is
     *                                  empty or {@code null}, or if the specified authentication
     *                                  type is {@code null} or an empty string.
     */
    public abstract void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket)
            throws CertificateException;

    /**
     * Checks whether the specified certificate chain (partial or complete) can
     * be validated and is trusted for client authentication for the specified
     * authentication type.
     * <p/>
     * If the {@code engine} is supplied, its {@link SSLParameters} will be
     * checked for endpoint identification.
     *
     * @param chain    the certificate chain to validate.
     * @param authType the authentication type used.
     * @param engine   the engine from which to check the {@link SSLParameters}
     * @throws CertificateException     if the certificate chain can't be validated
     *                                  or isn't trusted.
     * @throws IllegalArgumentException if the specified certificate chain is
     *                                  empty or {@code null}, or if the specified authentication
     *                                  type is {@code null} or an empty string.
     */
    public abstract void checkClientTrusted(X509Certificate[] chain, String authType,
                                            SSLEngine engine) throws CertificateException;

    /**
     * Checks whether the specified certificate chain (partial or complete) can
     * be validated and is trusted for server authentication for the specified
     * key exchange algorithm.
     * <p/>
     * If the {@code engine} is supplied, its {@link SSLParameters} will be
     * checked for endpoint identification.
     *
     * @param chain    the certificate chain to validate.
     * @param authType the authentication type used.
     * @param engine   the engine from which to check the {@link SSLParameters}
     * @throws CertificateException     if the certificate chain can't be validated
     *                                  or isn't trusted.
     * @throws IllegalArgumentException if the specified certificate chain is
     *                                  empty or {@code null}, or if the specified authentication
     *                                  type is {@code null} or an empty string.
     */
    public abstract void checkServerTrusted(X509Certificate[] chain, String authType,
                                            SSLEngine engine) throws CertificateException;
}
