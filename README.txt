===========================================================
Welcome to Apache Tika  <http://incubator.apache.org/tika/>
===========================================================

Apache Tika is a toolkit for detecting and extracting metadata and
structured text content from various documents using existing parser
libraries. 

Apache Tika is an effort undergoing incubation at The Apache Software
Foundation (ASF), sponsored by the Apache Lucene PMC. Incubation is
required of all newly accepted projects until a further review indicates
that the infrastructure, communications, and decision making process have
stabilized in a manner consistent with other successful ASF projects.
While incubation status is not necessarily a reflection of the completeness
or stability of the code, it does indicate that the project has yet to be
fully endorsed by the ASF.

See http://incubator.apache.org/projects/tika.html for the current
incubation status of the Apache Tika project.

License (see also LICENSE.txt)
==============================

Collective work: Copyright 2007-2008 The Apache Software Foundation.

Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Apache Tika includes a number of subcomponents with separate copyright
notices and license terms. Your use of these subcomponents is subject to
the terms and conditions of the licenses listed in the LICENSE.txt file.

Export control
==============

This distribution includes cryptographic software.  The country in  which
you currently reside may have restrictions on the import,  possession, use,
and/or re-export to another country, of encryption software.  BEFORE using
any encryption software, please  check your country's laws, regulations and
policies concerning the import, possession, or use, and re-export of\
encryption software, to  see if this is permitted.  See
<http://www.wassenaar.org/> for more information.

The U.S. Government Department of Commerce, Bureau of Industry and
Security (BIS), has classified this software as Export Commodity Control
Number (ECCN) 5D002.C.1, which includes information security software using
or performing cryptographic functions with asymmetric algorithms.  The form
and manner of this Apache Software Foundation distribution makes it eligible
for export under the License Exception ENC Technology Software Unrestricted
(TSU) exception (see the BIS Export Administration Regulations, Section
740.13) for both object code and source code.

The following provides more details on the included cryptographic software:

    Apache Tika uses the Bouncy Castle generic encryption libraries for
    extracting text content and metadata from encrypted PDF files.
    See http://www.bouncycastle.org/ for more details on Bouncy Castle.

Building Tika
=============

You can build the Tika sources using the Maven 2 build system. Execute the
following command in the Tika source directory to build the sources and
to install the resulting jar artifact in your local Maven repository:

    mvn install

See the Maven web site at http://maven.apache.org/ for more instructions
and the latest Maven downloads. 

Mailing Lists
=============

Discussion about the Tika project takes place on the development mailing
list tika-dev@incubator.apache.org. The list is open to anyone and
publicly archived. You can subscribe the mailing list by sending a
message to tika-dev-subscribe@incubator.apache.org, and unsubscribe by
sending a message to tika-dev-unsubscribe@incubator.apache.org. To receive
more instructions, send a message to tika-dev-help@incubator.apache.org.

Issue Tracker
=============

If you encounter errors in Tika or want to suggest an improvement or
a new feature, please visit the Tika issue tracker at
https://issues.apache.org/jira/browse/TIKA. There you can also find the
latest information on known issues and recent bug fixes and enhancements.

Updating the Tika web site
==========================

Here's how to update the live Tika website (http://incubator.apache.org/tika/)

    1) Edit the content found in src/site

    2) Run "mvn site" to generate the website pages

    3) Check the new content at target/site/index.html

    4) Checkout https://svn.apache.org/repos/asf/incubator/tika/site
       and update the changed pages there

    5) Commit your changes, both here and in the tika/site module

    6) To activate the changes on the live website, login to 
       people.apache.org and run:

           umask 002; svn update /www/incubator.apache.org/tika

    7) That directory is replicated to the live website every few hours,
       so your changes can take some time to be live.

Easy and fun, isn't it? ;-)

This will get better once Tika graduates from the Incubator.
