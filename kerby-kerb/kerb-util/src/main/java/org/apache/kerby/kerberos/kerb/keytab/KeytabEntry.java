/**
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.kerby.kerberos.kerb.keytab;

import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KeytabEntry {
    private PrincipalName principal;
    private KerberosTime timestamp;
    private int kvno;
    private EncryptionKey key;

    public KeytabEntry(PrincipalName principal, KerberosTime timestamp,
                       int kvno, EncryptionKey key) {
        this.principal = principal;
        this.timestamp = timestamp;
        this.kvno = kvno;
        this.key = key;
    }

    public KeytabEntry() {

    }

    public void load(KeytabInputStream kis, int version) throws IOException {
        this.principal = kis.readPrincipal(version);

        this.timestamp = kis.readTime();

        this.kvno = kis.readByte();

        this.key = kis.readKey();
    }

    public void store(KeytabOutputStream kos) throws IOException {
        byte[] body = null;

        // compute entry body content first so that to get and write the size
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        KeytabOutputStream subKos = new KeytabOutputStream(baos);
        writeBody(subKos, 0); // todo: consider the version
        subKos.flush();
        body = baos.toByteArray();

        kos.writeInt(body.length);
        kos.write(body);
    }

    public EncryptionKey getKey() {
        return key;
    }

    public int getKvno() {
        return kvno;
    }

    public PrincipalName getPrincipal() {
        return principal;
    }

    public KerberosTime getTimestamp() {
        return timestamp;
    }

    public void writeBody(KeytabOutputStream kos, int version) throws IOException {
        kos.writePrincipal(principal, version);

        kos.writeTime(timestamp);

        kos.writeByte(kvno);

        kos.writeKey(key, version);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        KeytabEntry that = (KeytabEntry) o;

        if (kvno != that.kvno) {
            return false;
        }
        if (!key.equals(that.key)) {
            return false;
        }
        if (!principal.equals(that.principal)) {
            return false;
        }
        if (!timestamp.equals(that.timestamp)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = principal.hashCode();
        result = 31 * result + timestamp.hashCode();
        result = 31 * result + kvno;
        result = 31 * result + key.hashCode();
        return result;
    }
}
