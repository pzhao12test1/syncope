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
package org.apache.syncope.core.provisioning.api;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.syncope.common.lib.to.AnyTO;
import org.apache.syncope.common.lib.to.RealmTO;
import org.apache.syncope.core.persistence.api.entity.Any;
import org.apache.syncope.core.persistence.api.entity.AnyUtils;
import org.apache.syncope.core.persistence.api.entity.PlainAttrValue;
import org.apache.syncope.core.persistence.api.entity.Realm;
import org.apache.syncope.core.persistence.api.entity.resource.Item;
import org.apache.syncope.core.persistence.api.entity.resource.OrgUnit;
import org.apache.syncope.core.persistence.api.entity.resource.Provision;
import org.identityconnectors.framework.common.objects.Attribute;

public interface MappingManager {

    /**
     * Get connObjectKey internal value.
     *
     * @param any any object
     * @param provision provision information
     * @return connObjectKey internal value
     */
    Optional<String> getConnObjectKeyValue(Any<?> any, Provision provision);

    /**
     * Get connObjectKey internal value.
     *
     * @param realm realm
     * @param orgUnit orgUnit information
     * @return connObjectKey internal value
     */
    String getConnObjectKeyValue(Realm realm, OrgUnit orgUnit);

    /**
     * Get attribute values for the given {@link Item} and any object.
     *
     * @param provision provision information
     * @param mapItem mapping item
     * @param intAttrName int attr name
     * @param any any object
     * @return attribute values.
     */
    List<PlainAttrValue> getIntValues(Provision provision, Item mapItem, IntAttrName intAttrName, Any<?> any);

    /**
     * Prepare attributes for sending to a connector instance.
     *
     * @param any given any object
     * @param password clear-text password
     * @param changePwd whether password should be included for propagation attributes or not
     * @param enable whether any object must be enabled or not
     * @param provision provision information
     * @return connObjectLink + prepared attributes
     */
    Pair<String, Set<Attribute>> prepareAttrs(
            Any<?> any, String password, boolean changePwd, Boolean enable, Provision provision);

    /**
     * Prepare attributes for sending to a connector instance.
     *
     * @param realm Realm
     * @param orgUnit provision information
     * @return connObjectLink + prepared attributes
     */
    Pair<String, Set<Attribute>> prepareAttrs(Realm realm, OrgUnit orgUnit);

    /**
     * Set attribute values, according to the given {@link Item}, to any object from attribute received from
     * connector.
     *
     * @param <T> any object
     * @param mapItem mapping item
     * @param attr attribute received from connector
     * @param anyTO any object
     * @param anyUtils any utils
     */
    <T extends AnyTO> void setIntValues(Item mapItem, Attribute attr, T anyTO, AnyUtils anyUtils);

    /**
     * Set attribute values, according to the given {@link Item}, to realm from attribute received from
     * connector.
     *
     * @param orgUnitItem mapping item
     * @param attr attribute received from connector
     * @param realmTO realm
     */
    void setIntValues(Item orgUnitItem, Attribute attr, RealmTO realmTO);

}
