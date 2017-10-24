begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|sax
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_comment
comment|/**  * This class provides a collection of the most important technical standard organizations.  * The collection of standard organizations has been obtained from<a href="https://en.wikipedia.org/wiki/List_of_technical_standard_organisations">Wikipedia</a>.  * Currently, the list is composed of the most important international standard organizations, the regional standard organizations (i.e., Africa, Americas, Asia Pacific, Europe, and Middle East), and British and American standard organizations among the national-based ones.  *  */
end_comment

begin_class
specifier|public
class|class
name|StandardOrganizations
block|{
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|organizations
decl_stmt|;
static|static
block|{
name|organizations
operator|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
comment|//International standard organizations
name|organizations
operator|.
name|put
argument_list|(
literal|"3GPP"
argument_list|,
literal|"3rd Generation Partnership Project"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"3GPP2"
argument_list|,
literal|"3rd Generation Partnership Project 2"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"Accellera"
argument_list|,
literal|"Accellera Organization"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"A4L"
argument_list|,
literal|"Access for Learning Community (formerly known as the Schools Interoperability Framework)"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"AES"
argument_list|,
literal|"Audio Engineering Society"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"AIIM"
argument_list|,
literal|"Association for Information and Image Management"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"ASAM"
argument_list|,
literal|"Association for Automation and Measuring Systems - Automotive technology"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"ASHRAE"
argument_list|,
literal|"American Society of Heating, Refrigerating and Air-Conditioning Engineers (ASHRAE is an international organization, despite its name)"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"ASME"
argument_list|,
literal|"formerly The American Society of Mechanical Engineers"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"ASTM"
argument_list|,
literal|"ASTM (American Society for Testing and Materials) International"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"ATIS"
argument_list|,
literal|"Alliance for Telecommunications Industry Solutions"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"AUTOSAR"
argument_list|,
literal|"Automotive technology"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"BIPM, CGPM, and CIPM"
argument_list|,
literal|"Bureau International des Poids et Mesures and the related organizations established under the Metre Convention of 1875."
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"CableLabs"
argument_list|,
literal|"Cable Television Laboratories"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"CCSDS"
argument_list|,
literal|"Consultative Committee for Space Data Sciences"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"CISPR"
argument_list|,
literal|"International Special Committee on Radio Interference"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"CFA"
argument_list|,
literal|"Compact flash association"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"DCMI"
argument_list|,
literal|"Dublin Core Metadata Initiative"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"DMTF"
argument_list|,
literal|"Distributed Management Task Force"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"Ecma International"
argument_list|,
literal|"Ecma International (previously called ECMA)"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"EKOenergy"
argument_list|,
literal|"EKOenergy Network managed by environmental NGOs"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"FAI"
argument_list|,
literal|"Fédération Aéronautique Internationale"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"GlobalPlatform"
argument_list|,
literal|"Secure element and TEE standards"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"GS1"
argument_list|,
literal|"Global supply chain standards (identification numbers, barcodes, electronic commerce transactions, RFID)"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"HGI"
argument_list|,
literal|"Home Gateway Initiative"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"HFSB"
argument_list|,
literal|"Hedge Fund Standards Board"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"IATA"
argument_list|,
literal|"International Air Transport Association"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"IAU*"
argument_list|,
literal|"International Arabic Union"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"ICAO"
argument_list|,
literal|"International Civil Aviation Organization"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"IEC"
argument_list|,
literal|"International Electrotechnical Commission"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"IEEE"
argument_list|,
literal|"Institute of Electrical and Electronics Engineers"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"IEEE-SA"
argument_list|,
literal|"IEEE Standards Association"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"IETF"
argument_list|,
literal|"Internet Engineering Task Force"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"IFOAM"
argument_list|,
literal|"International Federation of Organic Agriculture Movements"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"IFSWF"
argument_list|,
literal|"International Forum of Sovereign Wealth Funds"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"IMO"
argument_list|,
literal|"International Maritime Organization"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"IMS"
argument_list|,
literal|"IMS Global Learning Consortium"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"ISO"
argument_list|,
literal|"International Organization for Standardization"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"IPTC"
argument_list|,
literal|"International Press Telecommunications Council"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"ITU"
argument_list|,
literal|"The International Telecommunication Union"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"ITU-R"
argument_list|,
literal|"ITU Radiocommunications Sector (formerly known as CCIR)"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"CCIR"
argument_list|,
literal|"Comité Consultatif International pour la Radio, a forerunner of the ITU-R"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"ITU-T"
argument_list|,
literal|"ITU Telecommunications Sector (formerly known as CCITT)"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"CCITT"
argument_list|,
literal|"Comité Consultatif International Téléphonique et Télégraphique, renamed ITU-T in 1993"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"ITU-D"
argument_list|,
literal|"ITU Telecom Development (formerly known as BDT)"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"BDT"
argument_list|,
literal|"Bureau de développement des télécommunications, renamed ITU-D"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"IUPAC"
argument_list|,
literal|"International Union of Pure and Applied Chemistry"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"Liberty Alliance"
argument_list|,
literal|"Liberty Alliance"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"Media Grid"
argument_list|,
literal|"Media Grid Standards Organization"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"NACE International"
argument_list|,
literal|"Formerly known as National Association of Corrosion Engineers"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"OASIS"
argument_list|,
literal|"Organization for the Advancement of Structured Information Standards"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"OGC"
argument_list|,
literal|"Open Geospatial Consortium"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"OHICC"
argument_list|,
literal|"Organization of Hotel Industry Classification& Certification"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"OMA"
argument_list|,
literal|"Open Mobile Alliance"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"OMG"
argument_list|,
literal|"Object Management Group"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"OGF"
argument_list|,
literal|"Open Grid Forum (merger of Global Grid Forum (GGF) and Enterprise Grid Alliance (EGA))"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"GGF"
argument_list|,
literal|"Global Grid Forum"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"EGA"
argument_list|,
literal|"Enterprise Grid Alliance"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"OpenTravel Alliance"
argument_list|,
literal|"OpenTravel Alliance (previously known as OTA)"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"OTA"
argument_list|,
literal|"OpenTravel Alliance"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"OSGi"
argument_list|,
literal|"OSGi Alliance"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"PESC"
argument_list|,
literal|"P20 Education Standards Council[1]"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"SAI"
argument_list|,
literal|"Social Accountability International"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"SDA"
argument_list|,
literal|"Secure Digital Association"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"SNIA"
argument_list|,
literal|"Storage Networking Industry Association"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"SMPTE"
argument_list|,
literal|"Society of Motion Picture and Television Engineers"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"SSDA"
argument_list|,
literal|"Solid State Drive Alliance"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"The Open Group"
argument_list|,
literal|"The Open Group"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"TIA"
argument_list|,
literal|"Telecommunications Industry Association"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"TM Forum"
argument_list|,
literal|"Telemanagement Forum"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"UIC"
argument_list|,
literal|"International Union of Railways"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"UL"
argument_list|,
literal|"Underwriters Laboratories"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"UPU"
argument_list|,
literal|"Universal Postal Union"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"WMO"
argument_list|,
literal|"World Meteorological Organization"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"W3C"
argument_list|,
literal|"World Wide Web Consortium"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"WSA"
argument_list|,
literal|"Website Standards Association"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"WHO"
argument_list|,
literal|"World Health Organization"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"XSF"
argument_list|,
literal|"The XMPP Standards Foundation"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"FAO"
argument_list|,
literal|"Food and Agriculture Organization"
argument_list|)
expr_stmt|;
comment|//Regional standards organizations
comment|//Africa
name|organizations
operator|.
name|put
argument_list|(
literal|"ARSO"
argument_list|,
literal|"African Regional Organization for Standarization"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"SADCSTAN"
argument_list|,
literal|"Southern African Development Community (SADC) Cooperation in Standarization"
argument_list|)
expr_stmt|;
comment|//Americas
name|organizations
operator|.
name|put
argument_list|(
literal|"COPANT"
argument_list|,
literal|"Pan American Standards Commission"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"AMN"
argument_list|,
literal|"MERCOSUR Standardization Association"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"CROSQ"
argument_list|,
literal|"CARICOM Regional Organization for Standards and Quality"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"AAQG"
argument_list|,
literal|"America's Aerospace Quality Group"
argument_list|)
expr_stmt|;
comment|//Asia Pacific
name|organizations
operator|.
name|put
argument_list|(
literal|"PASC"
argument_list|,
literal|"Pacific Area Standards Congress"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"ACCSQ"
argument_list|,
literal|"ASEAN Consultative Committee for Standards and Quality"
argument_list|)
expr_stmt|;
comment|//Europe
name|organizations
operator|.
name|put
argument_list|(
literal|"RoyalCert"
argument_list|,
literal|"RoyalCert International Registrars"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"CEN"
argument_list|,
literal|"European Committee for Standardization"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"CENELEC"
argument_list|,
literal|"European Committee for Electrotechnical Standardization"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"URS"
argument_list|,
literal|"United Registrar of Systems, UK"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"ETSI"
argument_list|,
literal|"European Telecommunications Standards Institute"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"EASC"
argument_list|,
literal|"Euro-Asian Council for Standardization, Metrology and Certification"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"IRMM"
argument_list|,
literal|"Institute for Reference Materials and Measurements (European Union)"
argument_list|)
expr_stmt|;
comment|//Middle East
name|organizations
operator|.
name|put
argument_list|(
literal|"AIDMO"
argument_list|,
literal|"Arab Industrial Development and Mining Organization"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"IAU"
argument_list|,
literal|"International Arabic Union"
argument_list|)
expr_stmt|;
comment|//Nationally-based standards organizations
comment|//United Kingdom
name|organizations
operator|.
name|put
argument_list|(
literal|"BSI"
argument_list|,
literal|"British Standards Institution aka BSI Group"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"DStan"
argument_list|,
literal|"UK Defence Standardization"
argument_list|)
expr_stmt|;
comment|//United States of America
name|organizations
operator|.
name|put
argument_list|(
literal|"ANSI"
argument_list|,
literal|"American National Standards Institute"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"ACI"
argument_list|,
literal|"American Concrete Institute"
argument_list|)
expr_stmt|;
name|organizations
operator|.
name|put
argument_list|(
literal|"NIST"
argument_list|,
literal|"National Institute of Standards and Technology"
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Returns the map containing the collection of the most important technical standard organizations. 	 *  	 * @return the map containing the collection of the most important technical standard organizations. 	 */
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getOrganizations
parameter_list|()
block|{
return|return
name|organizations
return|;
block|}
comment|/** 	 * Returns the regular expression containing the most important technical standard organizations. 	 *  	 * @return the regular expression containing the most important technical standard organizations. 	 */
specifier|public
specifier|static
name|String
name|getOrganzationsRegex
parameter_list|()
block|{
name|String
name|regex
init|=
literal|"("
operator|+
name|String
operator|.
name|join
argument_list|(
literal|"|"
argument_list|,
name|organizations
operator|.
name|keySet
argument_list|()
argument_list|)
operator|+
literal|")"
decl_stmt|;
return|return
name|regex
return|;
block|}
block|}
end_class

end_unit

