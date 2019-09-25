/*
 * Copyright (C) 2017 Thomas Wolf <thomas.wolf@paranor.ch>
 * and other copyright owners as documented in the project's IP log.
 *
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Distribution License v1.0 which
 * accompanies this distribution, is reproduced below, and is
 * available at http://www.eclipse.org/org/documents/edl-v10.php
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials provided
 *   with the distribution.
 *
 * - Neither the name of the Eclipse Foundation, Inc. nor the
 *   names of its contributors may be used to endorse or promote
 *   products derived from this software without specific prior
 *   written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.geekoosh.flyway;

import com.geekoosh.flyway.request.FlywayMethod;
import com.geekoosh.flyway.request.Request;
import org.eclipse.jgit.junit.http.AppServer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class FlywayHandlerPostgresTests extends GitSSLTestCase {
    @Rule
    public final PostgresRule postgresRule = new PostgresRule();

    @Rule
    public final EnvironmentVariables environmentVariables
            = new EnvironmentVariables();

    @Before
    public void setUp() throws Exception {
        super.setUp();
        pushFilesToMaster(
                Arrays.asList(
                        new GitFile(
                                getClass().getClassLoader().getResource("migrations/postgres/V1__init.sql"),
                                "migrations/V1__init.sql"
                        )
                )
        );
        /*addFilesToMaster(Arrays.asList(
                new GitFile("migrations/V1__init.sql", new File(
                        getClass().getClassLoader().getResource("migrations/postgres/V1__init.sql").getFile())
                )
        ));*/
    }

    private void testMigrate(String connectionString) throws SQLException {
        environmentVariables.set("GIT_REPOSITORY", getRepoUrl());
        environmentVariables.set("GIT_USERNAME", AppServer.username);
        environmentVariables.set("GIT_PASSWORD", AppServer.password);
        environmentVariables.set("DB_USERNAME", "username");
        environmentVariables.set("DB_PASSWORD", "password");
        environmentVariables.set("GIT_FOLDERS", "migrations");
        environmentVariables.set("DB_CONNECTION_STRING", connectionString);
        environmentVariables.set("FLYWAY_METHOD", FlywayMethod.MIGRATE.name());

        FlywayHandler flywayHandler = new FlywayHandler();
        Response response = flywayHandler.handleRequest(new Request(), null);
        Assert.assertEquals("1", response.getInfo().current().getVersion().toString());
        Assert.assertEquals(1, response.getInfo().applied().length);

        Connection con= postgresRule.getConnection();
        Statement stmt=con.createStatement();
        ResultSet rs=stmt.executeQuery("select column_name, data_type from information_schema.COLUMNS where TABLE_NAME='tasks';");
        rs.next();
        Assert.assertEquals(rs.getString(1), "task_id");
        Assert.assertEquals(rs.getString(2), "integer");

        rs=stmt.executeQuery("select * from flyway_schema_history;");
        rs.next();
        Assert.assertEquals(rs.getInt(1), 1);
        Assert.assertEquals(rs.getInt(2), 1);

        con.close();
    }

    @Test
    public void testMigratePostgres() throws SQLException {
        testMigrate(postgresRule.getConnectionString());
    }
}
