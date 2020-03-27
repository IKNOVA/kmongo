/*
 * Copyright (C) 2016/2020 Litote
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.litote.kmongo


import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.bson.codecs.configuration.CodecRegistry
import org.litote.kmongo.service.ClassMappingType

/**
 * Main object used to create a [MongoClient](https://api.mongodb.com/java/current/com/mongodb/MongoClient.html) instance.
 */
object KMongo {

    /**
     * Creates an instance based on a (single) mongodb node (localhost, default port).
     *
     * @return the mongo client
     */
    fun createClient(): MongoClient = MongoClients.create()

    /**
     * Creates a Mongo instance based on a (single) mongodb node.

     * @param host server to connect to in format host(:port)
     * @return the mongo client
     */
    fun createClient(host: String): MongoClient = MongoClients.create("$MONGO_PROTOCOL$host")

    /**
     * Creates a Mongo instance based on a (single) mongodb node (default port).

     * @param host    server to connect to in format host(:port)
     * @param options default query options
     * @return the mongo client
     */
    fun createClient(host: String, settings: MongoClientSettings): MongoClient = MongoClients.create(
            MongoClientSettings.builder(settings).applyConnectionString(
                    ConnectionString("$MONGO_PROTOCOL$host")
            ).build()
    )

    /**
     * Creates a Mongo instance based on a (single) mongodb node.

     * @param host the database's host address
     * @param port the port on which the database is running
     * @return the mongo client
     */
    fun createClient(host: String, port: Int): MongoClient = createClient("$host:$port")

    /**
     * Creates a Mongo instance based on a (single) mongodb node.

     * @param connectionString the settings
     * @return the mongo client
     */
    fun createClient(connectionString: ConnectionString): MongoClient = MongoClients.create(connectionString)

    /**
     * Creates a Mongo instance based on a (single) mongodb node

     * @param addr the database address
     * @see com.mongodb.ServerAddress
     * @return the mongo client
     */
    fun createClient(addr: ServerAddress): MongoClient = createClient(addr.host, addr.port)

    /**
     * Creates a Mongo instance based on a (single) mongodb node and a list of credentials

     * @param addr            the database address
     * @param credentialsList the list of credentials used to authenticate all connections
     * @return the mongo client
     *
     * @see com.mongodb.ServerAddress
     */
    fun createClient(addr: ServerAddress, credential: MongoCredential): MongoClient = MongoClients.create(
            MongoClientSettings.builder().credential(credential).applyConnectionString(
                    ConnectionString("$MONGO_PROTOCOL${addr.host}:${addr.port}")
            ).build()
    )

    /**
     * Creates a Mongo instance based on a (single) mongo node using a given ServerAddress and default options.

     * @param addr    the database address
     * @param options default options
     * @return the mongo client
     *
     * @see com.mongodb.ServerAddress
     */
    fun createClient(addr: ServerAddress, settings: MongoClientSettings): MongoClient = MongoClients.create(
            MongoClientSettings.builder(configureSettings(settings)).applyConnectionString(
                    ConnectionString("$MONGO_PROTOCOL${addr.host}:${addr.port}")
            ).build()
    )

    /**
     * Creates a Mongo instance based on a (single) mongo node using a given ServerAddress and default options.

     * @param addr            the database address
     * @param credentialsList the list of credentials used to authenticate all connections
     * @param options         default options
     * @return the mongo client
     *
     * @see com.mongodb.ServerAddress
     */
    fun createClient(
            addr: ServerAddress,
            credential: MongoCredential,
            settings: MongoClientSettings
    ): MongoClient = MongoClients.create(
            MongoClientSettings.builder(configureSettings(settings)).applyConnectionString(
                    ConnectionString("$MONGO_PROTOCOL${addr.host}:${addr.port}")
            ).credential(credential).build()
    )

    /**
     *
     * Creates a Mongo based on a list of replica set members or a list of mongos. It will find all members (the master will be used by
     * default). If you pass in a single server in the list, the driver will still function as if it is a replica set. If you have a
     * standalone server, use the Mongo(ServerAddress) constructor.

     *
     * If this is a list of mongos servers, it will pick the closest (lowest ping time) one to send all requests to, and automatically
     * fail over to the next server if the closest is down.

     * @param seeds Put as many servers as you can in the list and the system will figure out the rest.  This can either be a list of mongod
     *              servers in the same replica set or a list of mongos servers in the same sharded cluster.
     * @return the mongo client
     *
     * @see com.mongodb.ServerAddress
     */
    fun createClient(seeds: List<ServerAddress>): MongoClient = MongoClients.create(
            MongoClientSettings.builder().applyToClusterSettings { it.hosts(seeds) }.build()
    )

    /**
     *
     * Creates a Mongo based on a list of replica set members or a list of mongos. It will find all members (the master will be used by
     * default). If you pass in a single server in the list, the driver will still function as if it is a replica set. If you have a
     * standalone server, use the Mongo(ServerAddress) constructor.

     *
     * If this is a list of mongos servers, it will pick the closest (lowest ping time) one to send all requests to, and automatically
     * fail over to the next server if the closest is down.

     * @param seeds           Put as many servers as you can in the list and the system will figure out the rest.  This can either be a list
     *                        of mongod servers in the same replica set or a list of mongos servers in the same sharded cluster.
     *
     * @param credentialsList the list of credentials used to authenticate all connections
     * @return the mongo client
     *
     * @see com.mongodb.ServerAddress
     */
    fun createClient(seeds: List<ServerAddress>, credential: MongoCredential): MongoClient = MongoClients.create(
            MongoClientSettings.builder().applyToClusterSettings { it.hosts(seeds) }.credential(credential).build()
    )

    /**
     *
     * Creates a Mongo based on a list of replica set members or a list of mongos. It will find all members (the master will be used by
     * default). If you pass in a single server in the list, the driver will still function as if it is a replica set. If you have a
     * standalone server, use the Mongo(ServerAddress) constructor.

     *
     * If this is a list of mongos servers, it will pick the closest (lowest ping time) one to send all requests to, and automatically
     * fail over to the next server if the closest is down.

     * @param seeds   Put as many servers as you can in the list and the system will figure out the rest.  This can either be a list of
     *                mongod servers in the same replica set or a list of mongos servers in the same sharded cluster.
     *
     * @param options default options
     * @return the mongo client
     *
     * @see com.mongodb.ServerAddress
     */
    fun createClient(seeds: List<ServerAddress>, settings: MongoClientSettings): MongoClient = MongoClients.create(
            MongoClientSettings.builder(configureSettings(settings)).applyToClusterSettings { it.hosts(seeds) }.build()
    )

    /**
     *
     * Creates a Mongo based on a list of replica set members or a list of mongos. It will find all members (the master will be used by
     * default). If you pass in a single server in the list, the driver will still function as if it is a replica set. If you have a
     * standalone server, use the Mongo(ServerAddress) constructor.

     *
     * If this is a list of mongos servers, it will pick the closest (lowest ping time) one to send all requests to, and automatically
     * fail over to the next server if the closest is down.

     * @param seeds           Put as many servers as you can in the list and the system will figure out the rest.  This can either be a list
     *                        of mongod servers in the same replica set or a list of mongos servers in the same sharded cluster.
     *
     * @param credentialsList the list of credentials used to authenticate all connections
     * @param options         default options
     * @return the mongo client
     *
     * @see com.mongodb.ServerAddress
     */
    fun createClient(
            seeds: List<ServerAddress>,
            credential: MongoCredential,
            settings: MongoClientSettings
    ): MongoClient = MongoClients.create(
            MongoClientSettings.builder(configureSettings(settings)).applyToClusterSettings { it.hosts(seeds) }
                    .credential(credential).build()
    )

    private fun configureSettings(clientSettings: MongoClientSettings): MongoClientSettings =
            MongoClientSettings.builder(clientSettings).codecRegistry(configureRegistry(clientSettings.codecRegistry)).build()

    internal fun configureRegistry(codecRegistry: CodecRegistry = MongoClientSettings.getDefaultCodecRegistry()): CodecRegistry {
        return ClassMappingType.codecRegistry(codecRegistry)
    }
}

private const val MONGO_PROTOCOL = "mongodb://"
