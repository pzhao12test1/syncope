/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.client.console.rest;

import java.util.List;
import org.apache.syncope.common.lib.to.RealmTO;
import org.apache.syncope.common.rest.api.service.RealmService;
import org.springframework.stereotype.Component;

/**
 * Console client for invoking Rest Group's services.
 */
@Component
public class RealmRestClient extends BaseRestClient {

    private static final long serialVersionUID = -8549081557283519638L;

    public List<RealmTO> list() {
        return getService(RealmService.class).list();
    }

    public void create(final String parentPath, final RealmTO realmTO) {
        getService(RealmService.class).create(parentPath, realmTO);         
    }
}
