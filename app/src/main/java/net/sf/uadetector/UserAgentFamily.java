/*******************************************************************************
 * Copyright 2012 André Rouél
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sf.uadetector;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import net.sf.qualitycheck.Check;

/**
 * This enum represents the more commonly used user agent families. It will never be complete, but can assist in
 * identifying an user agent.
 * 
 * @author André Rouél
 */
public enum UserAgentFamily {

	/**
	 * Representation of an unknown User-Agent
	 * 
	 * <p>
	 * <strong>Attention</strong>: This is not a known User-Agent family, but only a placeholder.
	 */
	UNKNOWN("unknown", Pattern.compile("^$")),

	/**
	 * 192.comAgent
	 */
	_192_COMAGENT("192.comAgent", Pattern.compile("192.comAgent")),

	/**
	 * 2Bone LinkChecker
	 */
	_2BONE_LINKCHECKER("2Bone LinkChecker", Pattern.compile("2Bone LinkChecker")),

	/**
	 * 50.nu
	 */
	_50_NU("50.nu", Pattern.compile("50.nu")),

	/**
	 * 80legs
	 */
	_80LEGS("80legs", Pattern.compile("80legs")),

	/**
	 * A1 Sitemap Generator
	 */
	A1_SITEMAP_GENERATOR("A1 Sitemap Generator", Pattern.compile("A1 Sitemap Generator")),

	/**
	 * AB (Apache Bench)
	 */
	AB_APACHE_BENCH("AB (Apache Bench)", Pattern.compile("AB \\(Apache Bench\\)")),

	/**
	 * abby
	 */
	ABBY("abby", Pattern.compile("abby")),

	/**
	 * Abilon
	 */
	ABILON("Abilon", Pattern.compile("Abilon")),

	/**
	 * Abolimba
	 */
	ABOLIMBA("Abolimba", Pattern.compile("Abolimba")),

	/**
	 * Aboundexbot
	 */
	ABOUNDEXBOT("Aboundexbot", Pattern.compile("Aboundexbot")),

	/**
	 * AboutUsBot
	 */
	ABOUTUSBOT("AboutUsBot", Pattern.compile("AboutUsBot")),

	/**
	 * Abrave Spider
	 */
	ABRAVE_SPIDER("Abrave Spider", Pattern.compile("Abrave Spider")),

	/**
	 * ABrowse
	 */
	ABROWSE("ABrowse", Pattern.compile("ABrowse")),

	/**
	 * Accelobot
	 */
	ACCELOBOT("Accelobot", Pattern.compile("Accelobot")),

	/**
	 * Accoona-AI-Agent
	 */
	ACCOONA_AI_AGENT("Accoona-AI-Agent", Pattern.compile("Accoona-AI-Agent")),

	/**
	 * Acoo Browser
	 */
	ACOO_BROWSER("Acoo Browser", Pattern.compile("Acoo Browser")),

	/**
	 * AcoonBot
	 */
	ACOONBOT("AcoonBot", Pattern.compile("AcoonBot")),

	/**
	 * Acorn
	 */
	ACORN("Acorn", Pattern.compile("Acorn")),

	/**
	 * ActiveXperts Network Monitor
	 */
	ACTIVEXPERTS_NETWORK_MONITOR("ActiveXperts Network Monitor", Pattern.compile("ActiveXperts Network Monitor")),

	/**
	 * AddThis.com
	 */
	ADDTHIS_COM("AddThis.com", Pattern.compile("AddThis.com")),

	/**
	 * Adobe AIR runtime
	 */
	ADOBE_AIR_RUNTIME("Adobe AIR runtime", Pattern.compile("Adobe AIR runtime")),

	/**
	 * adressendeutschland.de
	 */
	ADRESSENDEUTSCHLAND_DE("adressendeutschland.de", Pattern.compile("adressendeutschland.de")),

	/**
	 * AdsBot-Google
	 */
	ADSBOT_GOOGLE("AdsBot-Google", Pattern.compile("AdsBot-Google")),

	/**
	 * AhrefsBot
	 */
	AHREFSBOT("AhrefsBot", Pattern.compile("AhrefsBot")),

	/**
	 * aiHitBot
	 */
	AIHITBOT("aiHitBot", Pattern.compile("aiHitBot")),

	/**
	 * aippie
	 */
	AIPPIE("aippie", Pattern.compile("aippie")),

	/**
	 * AirMail
	 */
	AIRMAIL("AirMail", Pattern.compile("AirMail")),

	/**
	 * Akregator
	 */
	AKREGATOR("Akregator", Pattern.compile("Akregator")),

	/**
	 * akula
	 */
	AKULA("akula", Pattern.compile("akula")),

	/**
	 * Alienforce
	 */
	ALIENFORCE("Alienforce", Pattern.compile("Alienforce")),

	/**
	 * Almaden
	 */
	ALMADEN("Almaden", Pattern.compile("Almaden")),

	/**
	 * Amagit.COM
	 */
	AMAGIT_COM("Amagit.COM", Pattern.compile("Amagit.COM")),

	/**
	 * Amaya
	 */
	AMAYA("Amaya", Pattern.compile("Amaya")),

	/**
	 * Amazon Silk
	 */
	AMAZON_SILK("Amazon Silk", Pattern.compile("(Amazon Silk|Mobile Silk)")),

	/**
	 * Amfibibot
	 */
	AMFIBIBOT("Amfibibot", Pattern.compile("Amfibibot")),

	/**
	 * amibot
	 */
	AMIBOT("amibot", Pattern.compile("amibot")),

	/**
	 * Amiga Aweb
	 */
	AMIGA_AWEB("Amiga Aweb", Pattern.compile("Amiga Aweb")),

	/**
	 * Amiga Voyager
	 */
	AMIGA_VOYAGER("Amiga Voyager", Pattern.compile("Amiga Voyager")),

	/**
	 * Android Browser
	 */
	ANDROID_BROWSER("Android Browser", Pattern.compile("(Android Browser|Android Webkit)", Pattern.CASE_INSENSITIVE)),

	/**
	 * Anemone
	 */
	ANEMONE("Anemone", Pattern.compile("Anemone")),

	/**
	 * Anonymouse.org
	 */
	ANONYMOUSE_ORG("Anonymouse.org", Pattern.compile("Anonymouse.org")),

	/**
	 * AntBot
	 */
	ANTBOT("AntBot", Pattern.compile("AntBot")),

	/**
	 * anw HTMLChecker
	 */
	ANW_HTMLCHECKER("anw HTMLChecker", Pattern.compile("anw HTMLChecker")),

	/**
	 * anw LoadControl
	 */
	ANW_LOADCONTROL("anw LoadControl", Pattern.compile("anw LoadControl")),

	/**
	 * AOL Explorer
	 */
	AOL_EXPLORER("AOL Explorer", Pattern.compile("AOL Explorer")),

	/**
	 * Apache internal dummy connection
	 */
	APACHE_INTERNAL_DUMMY_CONNECTION("Apache internal dummy connection", Pattern.compile("Apache internal dummy connection")),

	/**
	 * Apache Synapse
	 */
	APACHE_SYNAPSE("Apache Synapse", Pattern.compile("Apache Synapse")),

	/**
	 * Apercite
	 */
	APERCITE("Apercite", Pattern.compile("Apercite")),

	/**
	 * AportWorm
	 */
	APORTWORM("AportWorm", Pattern.compile("AportWorm")),

	/**
	 * Apple-PubSub
	 */
	APPLE_MAIL("Apple Mail", Pattern.compile("Apple Mail")),

	/**
	 * Apple-PubSub
	 */
	APPLE_PUBSUB("Apple-PubSub", Pattern.compile("Apple-PubSub")),

	/**
	 * arachnode.net
	 */
	ARACHNODE_NET("arachnode.net", Pattern.compile("arachnode.net")),

	/**
	 * archive.org_bot
	 */
	ARCHIVE_ORG_BOT("archive.org_bot", Pattern.compile("archive.org_bot")),

	/**
	 * Arora
	 */
	ARORA("Arora", Pattern.compile("Arora")),

	/**
	 * ASAHA Search Engine Turkey
	 */
	ASAHA_SEARCH_ENGINE_TURKEY("ASAHA Search Engine Turkey", Pattern.compile("ASAHA Search Engine Turkey")),

	/**
	 * Ask Jeeves/Teoma
	 */
	ASK_JEEVES_TEOMA("Ask Jeeves/Teoma", Pattern.compile("Ask Jeeves/Teoma")),

	/**
	 * Atomic Email Hunter
	 */
	ATOMIC_EMAIL_HUNTER("Atomic Email Hunter", Pattern.compile("Atomic Email Hunter")),

	/**
	 * Atomic Web Browser
	 */
	ATOMIC_WEB_BROWSER("Atomic Web Browser", Pattern.compile("Atomic Web Browser")),

	/**
	 * Avant Browser
	 */
	AVANT_BROWSER("Avant Browser", Pattern.compile("Avant Browser")),

	/**
	 * AvantGo
	 */
	AVANTGO("AvantGo", Pattern.compile("AvantGo")),

	/**
	 * Awasu
	 */
	AWASU("Awasu", Pattern.compile("Awasu")),

	/**
	 * Axel
	 */
	AXEL("Axel", Pattern.compile("Axel")),

	/**
	 * BabalooSpider
	 */
	BABALOOSPIDER("BabalooSpider", Pattern.compile("BabalooSpider")),

	/**
	 * BacklinkCrawler
	 */
	BACKLINKCRAWLER("BacklinkCrawler", Pattern.compile("BacklinkCrawler")),

	/**
	 * Bad-Neighborhood
	 */
	BAD_NEIGHBORHOOD("Bad-Neighborhood", Pattern.compile("Bad-Neighborhood")),

	/**
	 * Baidu Browser
	 */
	BAIDU_BROWSER("Baidu Browser", Pattern.compile("Baidu Browser")),

	/**
	 * Baiduspider
	 */
	BAIDUSPIDER("Baiduspider", Pattern.compile("Baiduspider")),

	/**
	 * Banshee
	 */
	BANSHEE("Banshee", Pattern.compile("Banshee")),

	/**
	 * Barca
	 */
	BARCA("Barca", Pattern.compile("Barca")),

	/**
	 * baypup
	 */
	BAYPUP("baypup", Pattern.compile("baypup")),

	/**
	 * BDFetch
	 */
	BDFETCH("BDFetch", Pattern.compile("BDFetch")),

	/**
	 * Beamrise
	 */
	BEAMRISE("Beamrise", Pattern.compile("Beamrise")),

	/**
	 * BecomeBot
	 */
	BECOMEBOT("BecomeBot", Pattern.compile("BecomeBot")),

	/**
	 * Beonex
	 */
	BEONEX("Beonex", Pattern.compile("Beonex")),

	/**
	 * Bigsearch.ca
	 */
	BIGSEARCH_CA("Bigsearch.ca", Pattern.compile("Bigsearch.ca")),

	/**
	 * bingbot
	 */
	BINGBOT("bingbot", Pattern.compile("bingbot")),

	/**
	 * BinGet
	 */
	BINGET("BinGet", Pattern.compile("BinGet")),

	/**
	 * bitlybot
	 */
	BITLYBOT("bitlybot", Pattern.compile("bitlybot")),

	/**
	 * biwec
	 */
	BIWEC("biwec", Pattern.compile("biwec")),

	/**
	 * bixo
	 */
	BIXO("bixo", Pattern.compile("bixo")),

	/**
	 * bixolabs
	 */
	BIXOLABS("bixocrawler", Pattern.compile("(bixocrawler|bixolabs)")),

	/**
	 * BlackBerry Browser
	 */
	BLACKBERRY_BROWSER("BlackBerry Browser", Pattern.compile("BlackBerry Browser")),

	/**
	 * Blackbird
	 */
	BLACKBIRD("Blackbird", Pattern.compile("Blackbird")),

	/**
	 * BlackHawk
	 */
	BLACKHAWK("BlackHawk", Pattern.compile("BlackHawk")),

	/**
	 * Blaiz-Bee
	 */
	BLAIZ_BEE("Blaiz-Bee", Pattern.compile("Blaiz-Bee")),

	/**
	 * Blazer
	 */
	BLAZER("Blazer", Pattern.compile("Blazer")),

	/**
	 * Blekkobot
	 */
	BLEKKOBOT("Blekkobot", Pattern.compile("Blekkobot")),

	/**
	 * BlinkaCrawler
	 */
	BLINKACRAWLER("BlinkaCrawler", Pattern.compile("BlinkaCrawler")),

	/**
	 * BlogBridge
	 */
	BLOGBRIDGE("BlogBridge", Pattern.compile("BlogBridge")),

	/**
	 * Bloggsi
	 */
	BLOGGSI("Bloggsi", Pattern.compile("Bloggsi")),

	/**
	 * Bloglines
	 */
	BLOGLINES("Bloglines", Pattern.compile("Bloglines")),

	/**
	 * BlogPulse
	 */
	BLOGPULSE("BlogPulse", Pattern.compile("BlogPulse")),

	/**
	 * bnf.fr_bot
	 */
	BNF_FR_BOT("bnf.fr_bot", Pattern.compile("bnf.fr_bot")),

	/**
	 * boitho.com-dc
	 */
	BOITHO_COM_DC("boitho.com-dc", Pattern.compile("boitho.com-dc")),

	/**
	 * Bolt
	 */
	BOLT("Bolt", Pattern.compile("Bolt")),

	/**
	 * Bookdog
	 */
	BOOKDOG("Bookdog", Pattern.compile("Bookdog")),

	/**
	 * BookmarkTracker
	 */
	BOOKMARKTRACKER("BookmarkTracker", Pattern.compile("BookmarkTracker")),

	/**
	 * bot-pge.chlooe.com
	 */
	BOT_PGE_CHLOOE_COM("bot-pge.chlooe.com", Pattern.compile("bot-pge.chlooe.com")),

	/**
	 * botmobi
	 */
	BOTMOBI("botmobi", Pattern.compile("botmobi")),

	/**
	 * BotOnParade
	 */
	BOTONPARADE("BotOnParade", Pattern.compile("BotOnParade")),

	/**
	 * Boxxe
	 */
	BOXXE("Boxxe", Pattern.compile("Boxxe")),

	/**
	 * BrownRecluse
	 */
	BROWNRECLUSE("BrownRecluse", Pattern.compile("BrownRecluse")),

	/**
	 * Browsershots
	 */
	BROWSERSHOTS("Browsershots", Pattern.compile("Browsershots")),

	/**
	 * BrowseX
	 */
	BROWSEX("BrowseX", Pattern.compile("BrowseX")),

	/**
	 * Browzar
	 */
	BROWZAR("Browzar", Pattern.compile("Browzar")),

	/**
	 * btbot
	 */
	BTBOT("btbot", Pattern.compile("btbot")),

	/**
	 * Bunjalloo
	 */
	BUNJALLOO("Bunjalloo", Pattern.compile("Bunjalloo")),

	/**
	 * Butterfly
	 */
	BUTTERFLY("Butterfly", Pattern.compile("Butterfly")),

	/**
	 * BuzzRankingBot
	 */
	BUZZRANKINGBOT("BuzzRankingBot", Pattern.compile("BuzzRankingBot")),

	/**
	 * Camino
	 */
	CAMINO("Camino", Pattern.compile("Camino")),

	/**
	 * CamontSpider
	 */
	CAMONTSPIDER("CamontSpider", Pattern.compile("CamontSpider")),

	/**
	 * CareerBot
	 */
	CAREERBOT("CareerBot", Pattern.compile("CareerBot")),

	/**
	 * ^Nail
	 */
	CARET_NAIL("^Nail", Pattern.compile("^Nail")),

	/**
	 * Castabot
	 */
	CASTABOT("Castabot", Pattern.compile("Castabot")),

	/**
	 * CatchBot
	 */
	CATCHBOT("CatchBot", Pattern.compile("CatchBot")),

	/**
	 * CazoodleBot
	 */
	CAZOODLEBOT("CazoodleBot", Pattern.compile("CazoodleBot")),

	/**
	 * CCBot
	 */
	CCBOT("CCBot", Pattern.compile("CCBot")),

	/**
	 * ccubee
	 */
	CCUBEE("ccubee", Pattern.compile("ccubee")),

	/**
	 * ChangeDetection
	 */
	CHANGEDETECTION("ChangeDetection", Pattern.compile("ChangeDetection(/\\d+(\\.\\d+)*)?", Pattern.CASE_INSENSITIVE)),

	/**
	 * Charlotte
	 */
	CHARLOTTE("Charlotte", Pattern.compile("Charlotte")),

	/**
	 * Charon
	 */
	CHARON("Charon", Pattern.compile("Charon")),

	/**
	 * Checkbot
	 */
	CHECKBOT("Checkbot", Pattern.compile("Checkbot")),

	/**
	 * Cheshire
	 */
	CHESHIRE("Cheshire", Pattern.compile("Cheshire")),

	/**
	 * Chilkat HTTP .NET
	 */
	CHILKAT_HTTP_NET("Chilkat HTTP .NET", Pattern.compile("Chilkat HTTP .NET")),

	/**
	 * Chrome
	 */
	CHROME("Chrome", Pattern.compile("Chrome")),

	/**
	 * Chrome Mobile
	 */
	CHROME_MOBILE("Chrome Mobile", Pattern.compile("Chrome Mobile")),

	/**
	 * Chromium
	 */
	CHROMIUM("Chromium", Pattern.compile("Chromium")),

	/**
	 * City4you
	 */
	CITY4YOU("City4you", Pattern.compile("City4you")),

	/**
	 * cityreview
	 */
	CITYREVIEW("cityreview", Pattern.compile("cityreview")),

	/**
	 * CJB.NET Proxy
	 */
	CJB_NET_PROXY("CJB.NET Proxy", Pattern.compile("CJB.NET Proxy")),

	/**
	 * Claws Mail GtkHtml2 plugin
	 */
	CLAWS_MAIL_GTKHTML2_PLUGIN("Claws Mail GtkHtml2 plugin", Pattern.compile("Claws Mail GtkHtml2 plugin")),

	/**
	 * CligooRobot
	 */
	CLIGOOROBOT("CligooRobot", Pattern.compile("CligooRobot")),

	/**
	 * Coast
	 */
	COAST("Coast", Pattern.compile("Coast")),

	/**
	 * coccoc
	 */
	COCCOC("coccoc", Pattern.compile("coccoc")),

	/**
	 * Columbus
	 */
	COLUMBUS("Columbus", Pattern.compile("Columbus")),

	/**
	 * Combine
	 */
	COMBINE("Combine", Pattern.compile("Combine")),

	/**
	 * CometBird
	 */
	COMETBIRD("CometBird", Pattern.compile("CometBird")),

	/**
	 * Comodo Dragon
	 */
	COMODO_DRAGON("Comodo Dragon", Pattern.compile("Comodo Dragon")),

	/**
	 * CompSpyBot - Competitive Spying and Scraping
	 */
	COMPSPYBOT("CompSpyBot/1.0", Pattern.compile("CompSpyBot(/\\d+(\\.\\d+)*)?")),

	/**
	 * Conkeror
	 */
	CONKEROR("Conkeror", Pattern.compile("Conkeror")),

	/**
	 * ConveraCrawler
	 */
	CONVERACRAWLER("ConveraCrawler", Pattern.compile("ConveraCrawler")),

	/**
	 * CoolNovo
	 */
	COOLNOVO("CoolNovo", Pattern.compile("CoolNovo")),

	/**
	 * copyright sheriff
	 */
	COPYRIGHT_SHERIFF("copyright sheriff", Pattern.compile("copyright sheriff")),

	/**
	 * CorePlayer
	 */
	COREPLAYER("CorePlayer", Pattern.compile("CorePlayer")),

	/**
	 * CorpusCrawler
	 */
	CORPUSCRAWLER("CorpusCrawler", Pattern.compile("CorpusCrawler")),

	/**
	 * Covario-IDS
	 */
	COVARIO_IDS("Covario-IDS", Pattern.compile("Covario-IDS")),

	/**
	 * CPG Dragonfly RSS Module
	 */
	CPG_DRAGONFLY_RSS_MODULE("CPG Dragonfly RSS Module", Pattern.compile("CPG Dragonfly RSS Module")),

	/**
	 * Crawler4j
	 */
	CRAWLER4J("Crawler4j", Pattern.compile("Crawler4j")),

	/**
	 * Crazy Browser
	 */
	CRAZY_BROWSER("Crazy Browser", Pattern.compile("Crazy Browser")),

	/**
	 * csci_b659
	 */
	CSCI_B659("csci_b659", Pattern.compile("csci_b659")),

	/**
	 * CSE HTML Validator
	 */
	CSE_HTML_VALIDATOR("CSE HTML Validator", Pattern.compile("CSE HTML Validator")),

	/**
	 * cURL
	 */
	CURL("cURL", Pattern.compile("cURL")),

	/**
	 * Cyberduck
	 */
	CYBERDUCK("Cyberduck", Pattern.compile("Cyberduck")),

	/**
	 * Cynthia
	 */
	CYNTHIA("Cynthia", Pattern.compile("Cynthia")),

	/**
	 * D+
	 */
	D_PLUS("D+", Pattern.compile("D+")),

	/**
	 * DataFountains
	 */
	DATAFOUNTAINS("DataFountains", Pattern.compile("DataFountains")),

	/**
	 * DataparkSearch
	 */
	DATAPARKSEARCH("DataparkSearch", Pattern.compile("DataparkSearch")),

	/**
	 * Daumoa
	 */
	DAUMOA("Daumoa", Pattern.compile("Daumoa")),

	/**
	 * DBLBot
	 */
	DBLBOT("DBLBot", Pattern.compile("DBLBot")),

	/**
	 * DCPbot
	 */
	DCPBOT("DCPbot", Pattern.compile("DCPbot")),

	/**
	 * DealGates Bot
	 */
	DEALGATES_BOT("DealGates Bot", Pattern.compile("DealGates Bot")),

	/**
	 * Deepnet Explorer
	 */
	DEEPNET_EXPLORER("Deepnet Explorer", Pattern.compile("Deepnet Explorer")),

	/**
	 * del.icio.us-thumbnails
	 */
	DEL_ICIO_US_THUMBNAILS("del.icio.us-thumbnails", Pattern.compile("del.icio.us-thumbnails")),

	/**
	 * Dell Web Monitor
	 */
	DELL_WEB_MONITOR("Dell Web Monitor", Pattern.compile("Dell Web Monitor")),

	/**
	 * Demeter
	 */
	DEMETER("Demeter", Pattern.compile("Demeter")),

	/**
	 * DepSpid
	 */
	DEPSPID("DepSpid", Pattern.compile("DepSpid")),

	/**
	 * DeskBrowse
	 */
	DESKBROWSE("DeskBrowse", Pattern.compile("DeskBrowse")),

	/**
	 * Dillo
	 */
	DILLO("Dillo", Pattern.compile("Dillo")),

	/**
	 * Discoverybot is Discovery Engine's web crawler. It downloads text/html documents for use in building our full web
	 * search engine.
	 */
	DISCOBOT("discobot", Pattern.compile("(discobot|discoverybot)(/\\d+(\\.\\d+))?")),

	/**
	 * DKIMRepBot
	 */
	DKIMREPBOT("DKIMRepBot", Pattern.compile("DKIMRepBot")),

	/**
	 * DNS-Digger-Explorer
	 */
	DNS_DIGGER_EXPLORER("DNS-Digger-Explorer", Pattern.compile("DNS-Digger-Explorer")),

	/**
	 * DocZilla
	 */
	DOCZILLA("DocZilla", Pattern.compile("DocZilla")),

	/**
	 * Dolphin
	 */
	DOLPHIN("Dolphin", Pattern.compile("Dolphin")),

	/**
	 * DomainDB
	 */
	DOMAINDB("DomainDB", Pattern.compile("DomainDB")),

	/**
	 * Dooble
	 */
	DOOBLE("Dooble", Pattern.compile("Dooble")),

	/**
	 * Doris
	 */
	DORIS("Doris", Pattern.compile("Doris")),

	/**
	 * Dot TK - spider
	 */
	DOT_TK_SPIDER("Dot TK - spider", Pattern.compile("Dot TK - spider")),

	/**
	 * DotBot
	 */
	DOTBOT("DotBot", Pattern.compile("DotBot")),

	/**
	 * dotSemantic
	 */
	DOTSEMANTIC("dotSemantic", Pattern.compile("dotSemantic")),

	/**
	 * DownloadStudio
	 */
	DOWNLOADSTUDIO("DownloadStudio", Pattern.compile("DownloadStudio")),

	/**
	 * DripfeedBot
	 */
	DRIPFEEDBOT("DripfeedBot", Pattern.compile("DripfeedBot")),

	/**
	 * DuckDuckBot
	 */
	DUCKDUCKBOT("DuckDuckBot", Pattern.compile("DuckDuckBot")),

	/**
	 * DuckDuckPreview
	 */
	DUCKDUCKPREVIEW("DuckDuckPreview", Pattern.compile("DuckDuckPreview")),

	/**
	 * e-SocietyRobot
	 */
	E_SOCIETYROBOT("e-SocietyRobot", Pattern.compile("e-SocietyRobot")),

	/**
	 * EasyBib AutoCite
	 */
	EASYBIB_AUTOCITE("EasyBib AutoCite", Pattern.compile("EasyBib AutoCite")),

	/**
	 * eCairn-Grabber
	 */
	ECAIRN_GRABBER("eCairn-Grabber", Pattern.compile("eCairn-Grabber")),

	/**
	 * Edbrowse
	 */
	EDBROWSE("Edbrowse", Pattern.compile("Edbrowse")),

	/**
	 * EDI
	 */
	EDI("EDI", Pattern.compile("EDI")),

	/**
	 * EdisterBot
	 */
	EDISTERBOT("EdisterBot", Pattern.compile("EdisterBot")),

	/**
	 * egothor
	 */
	EGOTHOR("egothor", Pattern.compile("egothor")),

	/**
	 * ejupiter.com
	 */
	EJUPITER_COM("ejupiter.com", Pattern.compile("ejupiter.com")),

	/**
	 * Element Browser
	 */
	ELEMENT_BROWSER("Element Browser", Pattern.compile("Element Browser")),

	/**
	 * Elinks
	 */
	ELINKS("Elinks", Pattern.compile("Elinks")),

	/**
	 * EnaBot
	 */
	ENABOT("EnaBot", Pattern.compile("EnaBot")),

	/**
	 * Enigma browser
	 */
	ENIGMA_BROWSER("Enigma browser", Pattern.compile("Enigma browser")),

	/**
	 * Enterprise_Search
	 */
	ENTERPRISE_SEARCH("Enterprise_Search", Pattern.compile("Enterprise_Search")),

	/**
	 * envolk
	 */
	ENVOLK("envolk", Pattern.compile("envolk")),

	/**
	 * Epic
	 */
	EPIC("Epic", Pattern.compile("Epic")),

	/**
	 * Epiphany
	 */
	EPIPHANY("Epiphany", Pattern.compile("Epiphany")),

	/**
	 * Espial TV Browser
	 */
	ESPIAL_TV_BROWSER("Espial TV Browser", Pattern.compile("Espial TV Browser")),

	/**
	 * Eudora
	 */
	EUDORA("Eudora", Pattern.compile("Eudora")),

	/**
	 * EuripBot
	 */
	EURIPBOT("EuripBot", Pattern.compile("EuripBot")),

	/**
	 * Eurobot
	 */
	EUROBOT("Eurobot", Pattern.compile("Eurobot")),

	/**
	 * EventGuruBot
	 */
	EVENTGURUBOT("EventGuruBot", Pattern.compile("EventGuruBot")),

	/**
	 * EventMachine
	 */
	EVENTMACHINE("EventMachine", Pattern.compile("EventMachine")),

	/**
	 * Evolution/Camel.Stream
	 */
	EVOLUTION_CAMEL_STREAM("Evolution/Camel.Stream", Pattern.compile("Evolution/Camel.Stream")),

	/**
	 * EvriNid
	 */
	EVRINID("EvriNid", Pattern.compile("EvriNid")),

	/**
	 * Exabot
	 */
	EXABOT("Exabot", Pattern.compile("Exabot")),

	/**
	 * ExactSEEK
	 */
	EXACTSEEK("ExactSEEK", Pattern.compile("ExactSEEK")),

	/**
	 * Ezooms
	 */
	EZOOMS("Ezooms", Pattern.compile("Ezooms")),

	/**
	 * FacebookExternalHit
	 */
	FACEBOOKEXTERNALHIT("FacebookExternalHit", Pattern.compile("FacebookExternalHit")),

	/**
	 * factbot
	 */
	FACTBOT("factbot", Pattern.compile("factbot")),

	/**
	 * FairShare
	 */
	FAIRSHARE("FairShare", Pattern.compile("FairShare")),

	/**
	 * Falconsbot
	 */
	FALCONSBOT("Falconsbot", Pattern.compile("Falconsbot")),

	/**
	 * FAST Enterprise Crawler
	 */
	FAST_ENTERPRISE_CRAWLER("FAST Enterprise Crawler", Pattern.compile("FAST Enterprise Crawler")),

	/**
	 * FAST MetaWeb Crawler
	 */
	FAST_METAWEB_CRAWLER("FAST MetaWeb Crawler", Pattern.compile("FAST MetaWeb Crawler")),

	/**
	 * Fastladder FeedFetcher
	 */
	FASTLADDER_FEEDFETCHER("Fastladder FeedFetcher", Pattern.compile("Fastladder FeedFetcher")),

	/**
	 * FauBot
	 */
	FAUBOT("FauBot", Pattern.compile("FauBot")),

	/**
	 * favorstarbot
	 */
	FAVORSTARBOT("favorstarbot", Pattern.compile("favorstarbot")),

	/**
	 * Feed::Find
	 */
	FEED_FIND("Feed::Find", Pattern.compile("Feed::Find")),

	/**
	 * Feed Viewer
	 */
	FEED_VIEWER("Feed Viewer", Pattern.compile("Feed Viewer")),

	/**
	 * FeedCatBot
	 */
	FEEDCATBOT("FeedCatBot", Pattern.compile("FeedCatBot")),

	/**
	 * FeedDemon
	 */
	FEEDDEMON("FeedDemon", Pattern.compile("FeedDemon")),

	/**
	 * Feedfetcher-Google
	 */
	FEEDFETCHER_GOOGLE("Feedfetcher-Google", Pattern.compile("Feedfetcher-Google")),

	/**
	 * FeedFinder/bloggz.se
	 */
	FEEDFINDER_BLOGGZ_SE("FeedFinder/bloggz.se", Pattern.compile("FeedFinder/bloggz.se")),

	/**
	 * FeedParser
	 */
	FEEDPARSER("FeedParser", Pattern.compile("FeedParser")),

	/**
	 * FeedValidator
	 */
	FEEDVALIDATOR("FeedValidator", Pattern.compile("FeedValidator")),

	/**
	 * Findexa Crawler
	 */
	FINDEXA_CRAWLER("Findexa Crawler", Pattern.compile("Findexa Crawler")),

	/**
	 * findlinks
	 */
	FINDLINKS("findlinks", Pattern.compile("findlinks")),

	/**
	 * Firebird (old name for Firefox)
	 */
	FIREBIRD("Firebird (old name for Firefox)", Pattern.compile("Firebird \\(old name for Firefox\\)")),

	/**
	 * Firefox
	 */
	FIREFOX("Firefox", Pattern.compile("Firefox")),

	/**
	 * Firefox (BonEcho)
	 */
	FIREFOX_BONECHO("Firefox (BonEcho)", Pattern.compile("Firefox \\(BonEcho\\)")),

	/**
	 * Firefox (GranParadiso)
	 */
	FIREFOX_GRANPARADISO("Firefox (GranParadiso)", Pattern.compile("Firefox \\(GranParadiso\\)")),

	/**
	 * Firefox (Lorentz)
	 */
	FIREFOX_LORENTZ("Firefox (Lorentz)", Pattern.compile("Firefox \\(Lorentz\\)")),

	/**
	 * Firefox (Minefield)
	 */
	FIREFOX_MINEFIELD("Firefox (Minefield)", Pattern.compile("Firefox \\(Minefield\\)")),

	/**
	 * Firefox (Namoroka)
	 */
	FIREFOX_NAMOROKA("Firefox (Namoroka)", Pattern.compile("Firefox \\(Namoroka\\)")),

	/**
	 * Firefox (Shiretoko)
	 */
	FIREFOX_SHIRETOKO("Firefox (Shiretoko)", Pattern.compile("Firefox \\(Shiretoko\\)")),

	/**
	 * Fireweb Navigator
	 */
	FIREWEB_NAVIGATOR("Fireweb Navigator", Pattern.compile("Fireweb Navigator")),

	/**
	 * Flatland Industries Web Spider
	 */
	FLATLAND_INDUSTRIES_WEB_SPIDER("Flatland Industries Web Spider", Pattern.compile("Flatland Industries Web Spider")),

	/**
	 * flatlandbot
	 */
	FLATLANDBOT("flatlandbot", Pattern.compile("flatlandbot")),

	/**
	 * FlightDeckReportsBot
	 */
	FLIGHTDECKREPORTSBOT("FlightDeckReportsBot", Pattern.compile("FlightDeckReportsBot")),

	/**
	 * FlipboardProxy
	 */
	FLIPBOARDPROXY("FlipboardProxy", Pattern.compile("FlipboardProxy")),

	/**
	 * Flock
	 */
	FLOCK("Flock", Pattern.compile("Flock")),

	/**
	 * Flocke bot
	 */
	FLOCKE_BOT("Flocke bot", Pattern.compile("Flocke bot")),

	/**
	 * Fluid
	 */
	FLUID("Fluid", Pattern.compile("Fluid")),

	/**
	 * FlyCast
	 */
	FLYCAST("FlyCast", Pattern.compile("FlyCast")),

	/**
	 * FollowSite Bot
	 */
	FOLLOWSITE_BOT("FollowSite Bot", Pattern.compile("FollowSite Bot")),

	/**
	 * foobar2000
	 */
	FOOBAR2000("foobar2000", Pattern.compile("foobar2000")),

	/**
	 * Fooooo_Web_Video_Crawl
	 */
	FOOOOO_WEB_VIDEO_CRAWL("Fooooo_Web_Video_Crawl", Pattern.compile("Fooooo_Web_Video_Crawl")),

	/**
	 * Forschungsportal
	 */
	FORSCHUNGSPORTAL("Forschungsportal", Pattern.compile("Forschungsportal")),

	/**
	 * Francis
	 */
	FRANCIS("Francis", Pattern.compile("Francis")),

	/**
	 * Funambol Mozilla Sync Client
	 */
	FUNAMBOL_MOZILLA_SYNC_CLIENT("Funambol Mozilla Sync Client", Pattern.compile("Funambol Mozilla Sync Client")),

	/**
	 * Funambol Outlook Sync Client
	 */
	FUNAMBOL_OUTLOOK_SYNC_CLIENT("Funambol Outlook Sync Client", Pattern.compile("Funambol Outlook Sync Client")),

	/**
	 * FunnelBack
	 */
	FUNNELBACK("FunnelBack", Pattern.compile("FunnelBack")),

	/**
	 * FurlBot
	 */
	FURLBOT("FurlBot", Pattern.compile("FurlBot")),

	/**
	 * FyberSpider
	 */
	FYBERSPIDER("FyberSpider", Pattern.compile("FyberSpider")),

	/**
	 * g2crawler
	 */
	G2CRAWLER("g2crawler", Pattern.compile("g2crawler")),

	/**
	 * Gaisbot
	 */
	GAISBOT("Gaisbot", Pattern.compile("Gaisbot")),

	/**
	 * Galeon
	 */
	GALEON("Galeon", Pattern.compile("Galeon")),

	/**
	 * Gallent Search Spider
	 */
	GALLENT_SEARCH_SPIDER("Gallent Search Spider", Pattern.compile("Gallent Search Spider")),

	/**
	 * GarlikCrawler
	 */
	GARLIKCRAWLER("GarlikCrawler", Pattern.compile("GarlikCrawler")),

	/**
	 * GcMail
	 */
	GCMAIL("GcMail", Pattern.compile("GcMail")),

	/**
	 * genieBot
	 */
	GENIEBOT("genieBot", Pattern.compile("genieBot")),

	/**
	 * GeonaBot
	 */
	GEONABOT("GeonaBot", Pattern.compile("GeonaBot")),

	/**
	 * GetRight
	 */
	GETRIGHT("GetRight", Pattern.compile("GetRight")),

	/**
	 * Giant/1.0
	 */
	GIANT("Giant", Pattern.compile("Giant/(\\d+(\\.\\d+)*)")),

	/**
	 * Gigabot
	 */
	GIGABOT("Gigabot", Pattern.compile("Gigabot")),

	/**
	 * GingerCrawler
	 */
	GINGERCRAWLER("GingerCrawler", Pattern.compile("GingerCrawler")),

	/**
	 * Girafabot
	 */
	GIRAFABOT("Girafabot", Pattern.compile("Girafabot")),

	/**
	 * GlobalMojo
	 */
	GLOBALMOJO("GlobalMojo", Pattern.compile("GlobalMojo")),

	/**
	 * Gmail image proxy
	 */
	GMAIL_IMAGE_PROXY("Gmail image proxy", Pattern.compile("Gmail image proxy")),

	/**
	 * GnomeVFS
	 */
	GNOMEVFS("GnomeVFS", Pattern.compile("GnomeVFS")),

	/**
	 * GO Browser
	 */
	GO_BROWSER("GO Browser", Pattern.compile("GO Browser")),

	/**
	 * GOFORITBOT
	 */
	GOFORITBOT("GOFORITBOT", Pattern.compile("GOFORITBOT")),

	/**
	 * GoldenPod
	 */
	GOLDENPOD("GoldenPod", Pattern.compile("GoldenPod")),

	/**
	 * GOM Player
	 */
	GOM_PLAYER("GOM Player", Pattern.compile("GOM Player")),

	/**
	 * gonzo
	 */
	GONZO("gonzo", Pattern.compile("gonzo")),

	/**
	 * Google App Engine
	 */
	GOOGLE_APP_ENGINE("Google App Engine", Pattern.compile("Google App Engine")),

	/**
	 * Google Earth
	 */
	GOOGLE_EARTH("Google Earth", Pattern.compile("Google Earth")),

	/**
	 * Google Friend Connect
	 */
	GOOGLE_FRIEND_CONNECT("Google Friend Connect", Pattern.compile("Google Friend Connect")),

	/**
	 * Google Listen
	 */
	GOOGLE_LISTEN("Google Listen", Pattern.compile("Google Listen")),

	/**
	 * Google Rich Snippets Testing Tool
	 */
	GOOGLE_RICH_SNIPPETS_TESTING_TOOL("Google Rich Snippets Testing Tool", Pattern.compile("Google Rich Snippets Testing Tool")),

	/**
	 * Google Wireless Transcoder
	 */
	GOOGLE_WIRELESS_TRANSCODER("Google Wireless Transcoder", Pattern.compile("Google Wireless Transcoder")),

	/**
	 * Googlebot
	 */
	GOOGLEBOT("Googlebot", Pattern.compile("Googlebot")),

	/**
	 * Googlebot-Mobile
	 */
	GOOGLEBOT_MOBILE("Googlebot-Mobile", Pattern.compile("Googlebot-Mobile")),

	/**
	 * gPodder
	 */
	GPODDER("gPodder", Pattern.compile("gPodder")),

	/**
	 * GrapeshotCrawler
	 */
	GRAPESHOTCRAWLER("GrapeshotCrawler", Pattern.compile("GrapeshotCrawler")),

	/**
	 * GreatNews
	 */
	GREATNEWS("GreatNews", Pattern.compile("GreatNews")),

	/**
	 * GreenBrowser
	 */
	GREENBROWSER("GreenBrowser", Pattern.compile("GreenBrowser")),

	/**
	 * Gregarius
	 */
	GREGARIUS("Gregarius", Pattern.compile("Gregarius")),

	/**
	 * GSiteCrawler
	 */
	GSITECRAWLER("GSiteCrawler", Pattern.compile("GSiteCrawler")),

	/**
	 * GStreamer
	 */
	GSTREAMER("GStreamer", Pattern.compile("GStreamer")),

	/**
	 * GurujiBot
	 */
	GURUJIBOT("GurujiBot", Pattern.compile("GurujiBot")),

	/**
	 * Hailoobot
	 */
	HAILOOBOT("Hailoobot", Pattern.compile("Hailoobot")),

	/**
	 * HatenaScreenshot
	 */
	HATENASCREENSHOT("HatenaScreenshot", Pattern.compile("HatenaScreenshot")),

	/**
	 * HeartRails_Capture
	 */
	HEARTRAILS_CAPTURE("HeartRails_Capture", Pattern.compile("HeartRailsBot")),

	/**
	 * heritrix
	 */
	HERITRIX("heritrix", Pattern.compile("heritrix")),

	/**
	 * HiddenMarket
	 */
	HIDDENMARKET("HiddenMarket", Pattern.compile("HiddenMarket")),

	/**
	 * Holmes
	 */
	HOLMES("Holmes", Pattern.compile("Holmes")),

	/**
	 * HolmesBot
	 */
	HOLMESBOT("HolmesBot", Pattern.compile("HolmesBot")),

	/**
	 * HomeTags
	 */
	HOMETAGS("HomeTags", Pattern.compile("HomeTags")),

	/**
	 * HooWWWer
	 */
	HOOWWWER("HooWWWer", Pattern.compile("HooWWWer")),

	/**
	 * HostTracker.com
	 */
	HOSTTRACKER_COM("HostTracker", Pattern.compile("HostTracker")),

	/**
	 * HotJava
	 */
	HOTJAVA("HotJava", Pattern.compile("HotJava")),

	/**
	 * ht://Dig
	 */
	HT_DIG("ht://Dig", Pattern.compile("ht://Dig")),

	/**
	 * HTML2JPG
	 */
	HTML2JPG("HTML2JPG", Pattern.compile("HTML2JPG")),

	/**
	 * HTMLayout
	 */
	HTMLAYOUT("HTMLayout", Pattern.compile("HTMLayout")),

	/**
	 * HTMLParser
	 */
	HTMLPARSER("HTMLParser", Pattern.compile("HTMLParser")),

	/**
	 * HTTP nagios plugin
	 */
	HTTP_NAGIOS_PLUGIN("HTTP nagios plugin", Pattern.compile("HTTP nagios plugin")),

	/**
	 * HTTP_Request2
	 */
	HTTP_REQUEST2("HTTP_Request2", Pattern.compile("HTTP_Request2")),

	/**
	 * HTTrack
	 */
	HTTRACK("HTTrack", Pattern.compile("HTTrack")),

	/**
	 * HuaweiSymantecSpider
	 */
	HUAWEISYMANTECSPIDER("HuaweiSymantecSpider", Pattern.compile("HuaweiSymantecSpider")),

	/**
	 * Hv3
	 */
	HV3("Hv3", Pattern.compile("Hv3")),

	/**
	 * Hydra Browser
	 */
	HYDRA_BROWSER("Hydra Browser", Pattern.compile("Hydra Browser")),

	/**
	 * ia_archiver
	 */
	IA_ARCHIVER("ia_archiver", Pattern.compile("ia_archiver")),

	/**
	 * iaskspider
	 */
	IASKSPIDER("iaskspider", Pattern.compile("iaskspider")),

	/**
	 * IBrowse
	 */
	IBROWSE("IBrowse", Pattern.compile("IBrowse")),

	/**
	 * iCab
	 */
	ICAB("iCab", Pattern.compile("iCab")),

	/**
	 * iCatcher!
	 */
	ICATCHER("iCatcher!", Pattern.compile("iCatcher!")),

	/**
	 * ICC-Crawler
	 */
	ICC_CRAWLER("ICC-Crawler", Pattern.compile("ICC-Crawler")),

	/**
	 * ICE browser
	 */
	ICE_BROWSER("ICE browser", Pattern.compile("ICE browser")),

	/**
	 * IceApe
	 */
	ICEAPE("IceApe", Pattern.compile("IceApe")),

	/**
	 * IceCat
	 */
	ICECAT("IceCat", Pattern.compile("IceCat")),

	/**
	 * IceDragon: A faster, more secure version of Firefox
	 */
	ICEDRAGON("IceDragon", Pattern.compile("IceDragon")),

	/**
	 * IceWeasel
	 */
	ICEWEASEL("IceWeasel", Pattern.compile("IceWeasel")),

	/**
	 * ICF_Site_Crawler
	 */
	ICF_SITE_CRAWLER("ICF_Site_Crawler", Pattern.compile("ICF_Site_Crawler")),

	/**
	 * ichiro
	 */
	ICHIRO("ichiro", Pattern.compile("ichiro")),

	/**
	 * iCjobs
	 */
	ICJOBS("iCjobs", Pattern.compile("iCjobs")),

	/**
	 * Internet Explorer
	 */
	IE("IE", Pattern.compile("IE")),

	/**
	 * Internet Explorer Mobile
	 */
	IE_MOBILE("IE Mobile", Pattern.compile("IE Mobile")),

	/**
	 * Internet Explorer RSS reader
	 */
	IE_RSS_READER("IE RSS reader", Pattern.compile("IE RSS reader")),

	/**
	 * iGetter
	 */
	IGETTER("iGetter", Pattern.compile("iGetter")),

	/**
	 * iGooMap
	 */
	IGOOMAP("iGooMap", Pattern.compile("iGooMap")),

	/**
	 * IlseBot
	 */
	ILSEBOT("IlseBot", Pattern.compile("IlseBot")),

	/**
	 * IlTrovatore
	 */
	ILTROVATORE("IlTrovatore", Pattern.compile("IlTrovatore")),

	/**
	 * IlTrovatore-Setaccio
	 */
	ILTROVATORE_SETACCIO("IlTrovatore-Setaccio", Pattern.compile("IlTrovatore-Setaccio")),

	/**
	 * imbot
	 */
	IMBOT("imbot", Pattern.compile("imbot")),

	/**
	 * Indy Library
	 */
	INDY_LIBRARY("Indy Library", Pattern.compile("Indy Library")),

	/**
	 * Influencebot
	 */
	INFLUENCEBOT("Influencebot", Pattern.compile("Influencebot")),

	/**
	 * InfociousBot
	 */
	INFOCIOUSBOT("InfociousBot", Pattern.compile("InfociousBot")),

	/**
	 * Infohelfer
	 */
	INFOHELFER("Infohelfer", Pattern.compile("Infohelfer")),

	/**
	 * InternetSeer
	 */
	INTERNETSEER("InternetSeer", Pattern.compile("InternetSeer")),

	/**
	 * InternetSurfboard
	 */
	INTERNETSURFBOARD("InternetSurfboard", Pattern.compile("InternetSurfboard")),

	/**
	 * Ipselonbot
	 */
	IPSELONBOT("Ipselonbot", Pattern.compile("Ipselonbot")),

	/**
	 * iRider
	 */
	IRIDER("iRider", Pattern.compile("iRider")),

	/**
	 * IRLbot
	 */
	IRLBOT("IRLbot", Pattern.compile("IRLbot")),

	/**
	 * Iron
	 */
	IRON("Iron", Pattern.compile("Iron")),

	/**
	 * iSiloX
	 */
	ISILOX("iSiloX", Pattern.compile("iSiloX")),

	/**
	 * iSiloXC
	 */
	ISILOXC("iSiloXC", Pattern.compile("iSiloXC")),

	/**
	 * iTunes
	 */
	ITUNES("iTunes", Pattern.compile("iTunes")),

	/**
	 * iVideo
	 */
	IVIDEO("iVideo", Pattern.compile("iVideo")),

	/**
	 * IXR lib
	 */
	IXR_LIB("IXR lib", Pattern.compile("IXR lib")),

	/**
	 * JadynAve
	 */
	JADYNAVE("JadynAve", Pattern.compile("JadynAve")),

	/**
	 * JadynAveBot
	 */
	JADYNAVEBOT("JadynAveBot", Pattern.compile("JadynAveBot")),

	/**
	 * Jakarta Commons-HttpClient
	 */
	JAKARTA_COMMONS_HTTPCLIENT("Jakarta Commons-HttpClient", Pattern.compile("(Apache-HttpClient|Jakarta Commons-HttpClient)")),

	/**
	 * Jambot
	 */
	JAMBOT("Jambot", Pattern.compile("Jambot")),

	/**
	 * Jamcast
	 */
	JAMCAST("Jamcast", Pattern.compile("Jamcast")),

	/**
	 * Jasmine
	 */
	JASMINE("Jasmine", Pattern.compile("Jasmine")),

	/**
	 * Java
	 */
	JAVA("Java", Pattern.compile("Java")),

	/**
	 * JikeSpider
	 */
	JIKESPIDER("JikeSpider", Pattern.compile("JikeSpider")),

	/**
	 * Job Roboter Spider
	 */
	JOB_ROBOTER_SPIDER("Job Roboter Spider", Pattern.compile("Job Roboter Spider")),

	/**
	 * JoBo
	 */
	JOBO("JoBo", Pattern.compile("JoBo")),

	/**
	 * JS-Kit/Echo
	 */
	JS_KIT_ECHO("JS-Kit/Echo", Pattern.compile("JS-Kit/Echo")),

	/**
	 * JUST-CRAWLER
	 */
	JUST_CRAWLER("JUST-CRAWLER", Pattern.compile("JUST-CRAWLER")),

	/**
	 * Jyxobot
	 */
	JYXOBOT("Jyxobot", Pattern.compile("Jyxobot")),

	/**
	 * K-Meleon
	 */
	K_MELEON("K-Meleon", Pattern.compile("K-Meleon")),

	/**
	 * K-Ninja
	 */
	K_NINJA("K-Ninja", Pattern.compile("K-Ninja")),

	/**
	 * Kakle Bot
	 */
	KAKLE_BOT("Kakle Bot", Pattern.compile("Kakle Bot")),

	/**
	 * Kalooga
	 */
	KALOOGA("Kalooga", Pattern.compile("Kalooga")),

	/**
	 * Kapiko
	 */
	KAPIKO("Kapiko", Pattern.compile("Kapiko")),

	/**
	 * Karneval-Bot
	 */
	KARNEVAL_BOT("Karneval-Bot", Pattern.compile("Karneval-Bot")),

	/**
	 * Kazehakase
	 */
	KAZEHAKASE("Kazehakase", Pattern.compile("Kazehakase")),

	/**
	 * KeywenBot
	 */
	KEYWENBOT("KeywenBot", Pattern.compile("KeywenBot")),

	/**
	 * KeywordDensityRobot
	 */
	KEYWORDDENSITYROBOT("KeywordDensityRobot", Pattern.compile("KeywordDensityRobot")),

	/**
	 * Kindle Browser
	 */
	KINDLE_BROWSER("Kindle Browser", Pattern.compile("Kindle Browser")),

	/**
	 * Kirix Strata
	 */
	KIRIX_STRATA("Kirix Strata", Pattern.compile("Kirix Strata")),

	/**
	 * KKman
	 */
	KKMAN("KKman", Pattern.compile("KKman")),

	/**
	 * Klondike
	 */
	KLONDIKE("Klondike", Pattern.compile("Klondike")),

	/**
	 * Kongulo
	 */
	KONGULO("Kongulo", Pattern.compile("Kongulo")),

	/**
	 * Konqueror
	 */
	KONQUEROR("Konqueror", Pattern.compile("Konqueror")),

	/**
	 * KRetrieve
	 */
	KRETRIEVE("KRetrieve", Pattern.compile("KRetrieve")),

	/**
	 * Krugle
	 */
	KRUGLE("Krugle", Pattern.compile("Krugle")),

	/**
	 * ksibot
	 */
	KSIBOT("ksibot", Pattern.compile("ksibot")),

	/**
	 * Kylo
	 */
	KYLO("Kylo", Pattern.compile("Kylo")),

	/**
	 * L.webis
	 */
	L_WEBIS("L.webis", Pattern.compile("L.webis")),

	/**
	 * LapozzBot
	 */
	LAPOZZBOT("LapozzBot", Pattern.compile("LapozzBot")),

	/**
	 * Larbin
	 */
	LARBIN("Larbin", Pattern.compile("Larbin")),

	/**
	 * LBrowser
	 */
	LBROWSER("LBrowser", Pattern.compile("LBrowser")),

	/**
	 * LeechCraft
	 */
	LEECHCRAFT("LeechCraft", Pattern.compile("LeechCraft")),

	/**
	 * LemurWebCrawler
	 */
	LEMURWEBCRAWLER("LemurWebCrawler", Pattern.compile("LemurWebCrawler")),

	/**
	 * LexxeBot
	 */
	LEXXEBOT("LexxeBot", Pattern.compile("LexxeBot")),

	/**
	 * LFTP
	 */
	LFTP("LFTP", Pattern.compile("LFTP")),

	/**
	 * LG Web Browser
	 */
	LG_WEB_BROWSER("LG Web Browser", Pattern.compile("LG Web Browser")),

	/**
	 * LibSoup
	 */
	LIBSOUP("LibSoup", Pattern.compile("LibSoup")),

	/**
	 * libwww-perl
	 */
	LIBWWW_PERL("libwww-perl", Pattern.compile("libwww-perl")),

	/**
	 * Liferea
	 */
	LIFEREA("Liferea", Pattern.compile("Liferea")),

	/**
	 * Lijit
	 */
	LIJIT("Lijit", Pattern.compile("Lijit")),

	/**
	 * LinguaBot
	 */
	LINGUABOT("LinguaBot", Pattern.compile("LinguaBot")),

	/**
	 * Linguee Bot
	 */
	LINGUEE_BOT("Linguee Bot", Pattern.compile("Linguee Bot")),

	/**
	 * Link Valet Online
	 */
	LINK_VALET_ONLINE("Link Valet Online", Pattern.compile("Link Valet Online")),

	/**
	 * LinkAider
	 */
	LINKAIDER("LinkAider", Pattern.compile("LinkAider")),

	/**
	 * LinkbackPlugin for Laconica
	 */
	LINKBACKPLUGIN_FOR_LACONICA("LinkbackPlugin for Laconica", Pattern.compile("LinkbackPlugin for Laconica")),

	/**
	 * LinkChecker
	 */
	LINKCHECKER("LinkChecker", Pattern.compile("LinkChecker")),

	/**
	 * linkdex.com
	 */
	LINKDEX_COM("linkdex.com", Pattern.compile("linkdex.com")),

	/**
	 * LinkExaminer
	 */
	LINKEXAMINER("LinkExaminer", Pattern.compile("LinkExaminer")),

	/**
	 * Links
	 */
	LINKS("Links", Pattern.compile("Links")),

	/**
	 * linksmanager_bot
	 */
	LINKSMANAGER_BOT("linksmanager_bot", Pattern.compile("linksmanager_bot")),

	/**
	 * LinkWalker
	 */
	LINKWALKER("LinkWalker", Pattern.compile("LinkWalker")),

	/**
	 * livedoor ScreenShot
	 */
	LIVEDOOR_SCREENSHOT("livedoor ScreenShot", Pattern.compile("livedoor ScreenShot")),

	/**
	 * lmspider
	 */
	LMSPIDER("lmspider", Pattern.compile("lmspider")),

	/**
	 * Lobo
	 */
	LOBO("Lobo", Pattern.compile("Lobo")),

	/**
	 * lolifox
	 */
	LOLIFOX("lolifox", Pattern.compile("lolifox")),

	/**
	 * Lotus Notes
	 */
	LOTUS_NOTES("Lotus Notes", Pattern.compile("Lotus Notes")),

	/**
	 * Lunascape
	 */
	LUNASCAPE("Lunascape", Pattern.compile("Lunascape")),

	/**
	 * LWP::Simple
	 */
	LWP_SIMPLE("LWP::Simple", Pattern.compile("LWP::Simple")),

	/**
	 * Lynx
	 */
	LYNX("Lynx", Pattern.compile("Lynx")),

	/**
	 * Madfox
	 */
	MADFOX("Madfox", Pattern.compile("Madfox")),

	/**
	 * magpie-crawler
	 */
	MAGPIE_CRAWLER("magpie-crawler", Pattern.compile("magpie-crawler")),

	/**
	 * MagpieRSS
	 */
	MAGPIERSS("MagpieRSS", Pattern.compile("MagpieRSS")),

	/**
	 * Mahiti Crawler
	 */
	MAHITI_CRAWLER("Mahiti Crawler", Pattern.compile("Mahiti Crawler")),

	/**
	 * Mail.RU
	 */
	MAIL_RU("Mail.Ru", Pattern.compile("Mail.RU(_Bot)?(/\\d+(\\.\\d+)*)?", Pattern.CASE_INSENSITIVE)),

	/**
	 * Maple browser
	 */
	MAPLE_BROWSER("Maple browser", Pattern.compile("Maple browser")),

	/**
	 * Maxthon
	 */
	MAXTHON("Maxthon", Pattern.compile("Maxthon")),

	/**
	 * Mechanize
	 */
	MECHANIZE("Mechanize", Pattern.compile("Mechanize")),

	/**
	 * Megatext
	 */
	MEGATEXT("Megatext", Pattern.compile("Megatext")),

	/**
	 * MetaGeneratorCrawler
	 */
	METAGENERATORCRAWLER("MetaGeneratorCrawler", Pattern.compile("MetaGeneratorCrawler")),

	/**
	 * MetaJobBot
	 */
	METAJOBBOT("MetaJobBot", Pattern.compile("MetaJobBot")),

	/**
	 * MetamojiCrawler
	 */
	METAMOJICRAWLER("MetamojiCrawler", Pattern.compile("MetamojiCrawler")),

	/**
	 * Metaspinner/0.01
	 */
	METASPINNER("Metaspinner", Pattern.compile("Metaspinner(/\\d+(\\.\\d+)*)?")),

	/**
	 * MetaTagRobot
	 */
	METATAGROBOT("MetaTagRobot", Pattern.compile("MetaTagRobot")),

	/**
	 * MetaURI
	 */
	METAURI("MetaURI", Pattern.compile("MetaURI")),

	/**
	 * MIA Bot
	 */
	MIA_BOT("MIA Bot", Pattern.compile("MIA Bot")),

	/**
	 * MicroB
	 */
	MICROB("MicroB", Pattern.compile("MicroB")),

	/**
	 * Microsoft Edge
	 */
	MICROSOFT_EDGE("Microsoft Edge", Pattern.compile("Microsoft Edge")),

	/**
	 * Microsoft Edge
	 */
	MICROSOFT_EDGE_MOBILE("Microsoft Edge mobile", Pattern.compile("Microsoft Edge mobile")),

	/**
	 * Microsoft Office Existence Discovery
	 */
	MICROSOFT_OFFICE_EXISTENCE_DISCOVERY("Microsoft Office Existence Discovery", Pattern.compile("Microsoft Office Existence Discovery")),

	/**
	 * Microsoft WebDAV client
	 */
	MICROSOFT_WEBDAV_CLIENT("Microsoft WebDAV client", Pattern.compile("Microsoft WebDAV client")),

	/**
	 * Midori
	 */
	MIDORI("Midori", Pattern.compile("Midori")),

	/**
	 * Mini Browser
	 */
	MINI_BROWSER("Mini Browser", Pattern.compile("Mini Browser")),

	/**
	 * Minimo
	 */
	MINIMO("Minimo", Pattern.compile("Minimo")),

	/**
	 * miniRank
	 */
	MINIRANK("miniRank", Pattern.compile("miniRank")),

	/**
	 * Miro
	 */
	MIRO("Miro", Pattern.compile("Miro")),

	/**
	 * MJ12bot
	 */
	MJ12BOT("MJ12bot", Pattern.compile("MJ12bot")),

	/**
	 * MLBot
	 */
	MLBOT("MLBot", Pattern.compile("MLBot")),

	/**
	 * MnoGoSearch
	 */
	MNOGOSEARCH("MnoGoSearch", Pattern.compile("MnoGoSearch")),

	/**
	 * Moatbot
	 */
	MOATBOT("Moatbot", Pattern.compile("Moatbot")),

	/**
	 * moba-crawler
	 */
	MOBA_CRAWLER("moba-crawler", Pattern.compile("moba-crawler")),

	/**
	 * Mobile Firefox
	 */
	MOBILE_FIREFOX("Mobile Firefox", Pattern.compile("Firefox mobile")),

	/**
	 * Mobile Safari
	 */
	MOBILE_SAFARI("Mobile Safari", Pattern.compile("Safari mobile")),

	/**
	 * MojeekBot
	 */
	MOJEEKBOT("MojeekBot", Pattern.compile("MojeekBot")),

	/**
	 * Motoricerca-Robots.txt-Checker
	 */
	MOTORICERCA_ROBOTS_TXT_CHECKER("Motoricerca-Robots.txt-Checker", Pattern.compile("Motoricerca-Robots.txt-Checker")),

	/**
	 * Motorola Internet Browser
	 */
	MOTOROLA_INTERNET_BROWSER("Motorola Internet Browser", Pattern.compile("Motorola Internet Browser")),

	/**
	 * mozDex
	 */
	MOZDEX("mozDex", Pattern.compile("mozDex")),

	/**
	 * Mozilla
	 */
	MOZILLA("Mozilla", Pattern.compile("Mozilla")),

	/**
	 * Mp3Bot
	 */
	MP3BOT("Mp3Bot", Pattern.compile("Mp3Bot")),

	/**
	 * MPlayer
	 */
	MPLAYER("MPlayer", Pattern.compile("MPlayer")),

	/**
	 * MPlayer2
	 */
	MPLAYER2("MPlayer2", Pattern.compile("MPlayer2")),

	/**
	 * MQbot
	 */
	MQBOT("MQbot", Pattern.compile("MQbot")),

	/**
	 * MSNBot
	 */
	MSNBOT("MSNBot", Pattern.compile("MSNBot")),

	/**
	 * MSRBOT
	 */
	MSRBOT("MSRBOT", Pattern.compile("MSRBOT")),

	/**
	 * muCommander
	 */
	MUCOMMANDER("muCommander", Pattern.compile("muCommander")),

	/**
	 * Multi-Browser XP
	 */
	MULTI_BROWSER_XP("Multi-Browser XP", Pattern.compile("Multi-Browser XP")),

	/**
	 * MultiCrawler
	 */
	MULTICRAWLER("MultiCrawler", Pattern.compile("MultiCrawler")),

	/**
	 * Multipage Validator
	 */
	MULTIPAGE_VALIDATOR("Multipage Validator", Pattern.compile("Multipage Validator")),

	/**
	 * MultiZilla
	 */
	MULTIZILLA("MultiZilla", Pattern.compile("MultiZilla")),

	/**
	 * My Internet Browser
	 */
	MY_INTERNET_BROWSER("My Internet Browser", Pattern.compile("My Internet Browser")),

	/**
	 * MyFamilyBot
	 */
	MYFAMILYBOT("MyFamilyBot", Pattern.compile("MyFamilyBot")),

	/**
	 * Najdi.si
	 */
	NAJDI_SI("Najdi.si", Pattern.compile("Najdi.si")),

	/**
	 * NaverBot
	 */
	NAVERBOT("NaverBot", Pattern.compile("NaverBot")),

	/**
	 * navissobot
	 */
	NAVISSOBOT("navissobot", Pattern.compile("navissobot")),

	/**
	 * NCSA Mosaic
	 */
	NCSA_MOSAIC("NCSA Mosaic", Pattern.compile("NCSA Mosaic")),

	/**
	 * NerdByNature.Bot
	 */
	NERDBYNATURE_BOT("NerdByNature.Bot", Pattern.compile("NerdByNature.Bot")),

	/**
	 * nestReader
	 */
	NESTREADER("nestReader", Pattern.compile("nestReader")),

	/**
	 * NetBox
	 */
	NETBOX("NetBox", Pattern.compile("NetBox")),

	/**
	 * NetCaptor
	 */
	NETCAPTOR("NetCaptor", Pattern.compile("NetCaptor")),

	/**
	 * NetcraftSurveyAgent
	 */
	NETCRAFTSURVEYAGENT("NetcraftSurveyAgent", Pattern.compile("NetcraftSurveyAgent")),

	/**
	 * netEstate Crawler
	 */
	NETESTATE_CRAWLER("netEstate Crawler", Pattern.compile("netEstate Crawler")),

	/**
	 * NetFront
	 */
	NETFRONT("NetFront", Pattern.compile("NetFront")),

	/**
	 * NetFront Mobile Content Viewer
	 */
	NETFRONT_MOBILE_CONTENT_VIEWER("NetFront Mobile Content Viewer", Pattern.compile("NetFront Mobile Content Viewer")),

	/**
	 * Netintelligence LiveAssessment
	 */
	NETINTELLIGENCE_LIVEASSESSMENT("Netintelligence LiveAssessment", Pattern.compile("Netintelligence LiveAssessment")),

	/**
	 * NetNewsWire
	 */
	NETNEWSWIRE("NetNewsWire", Pattern.compile("NetNewsWire")),

	/**
	 * NetPositive
	 */
	NETPOSITIVE("NetPositive", Pattern.compile("NetPositive")),

	/**
	 * NetResearchServer
	 */
	NETRESEARCHSERVER("NetResearchServer", Pattern.compile("NetResearchServer")),

	/**
	 * Netscape Navigator
	 */
	NETSCAPE_NAVIGATOR("Netscape Navigator", Pattern.compile("Netscape Navigator")),

	/**
	 * Netseer
	 */
	NETSEER("Netseer", Pattern.compile("Netseer")),

	/**
	 * NetSurf
	 */
	NETSURF("NetSurf", Pattern.compile("NetSurf")),

	/**
	 * Netvibes feed reader
	 */
	NETVIBES_FEED_READER("Netvibes feed reader", Pattern.compile("Netvibes feed reader")),

	/**
	 * NetWhatCrawler
	 */
	NETWHATCRAWLER("NetWhatCrawler", Pattern.compile("NetWhatCrawler")),

	/**
	 * Newsbeuter
	 */
	NEWSBEUTER("Newsbeuter", Pattern.compile("Newsbeuter")),

	/**
	 * NewsBreak
	 */
	NEWSBREAK("NewsBreak", Pattern.compile("NewsBreak")),

	/**
	 * NewsFox
	 */
	NEWSFOX("NewsFox", Pattern.compile("NewsFox")),

	/**
	 * NewsGatorOnline
	 */
	NEWSGATORONLINE("NewsGatorOnline", Pattern.compile("NewsGatorOnline")),

	/**
	 * NextGenSearchBot
	 */
	NEXTGENSEARCHBOT("NextGenSearchBot", Pattern.compile("NextGenSearchBot")),

	/**
	 * nextthing.org
	 */
	NEXTTHING_ORG("nextthing.org", Pattern.compile("nextthing.org")),

	/**
	 * NFReader
	 */
	NFREADER("NFReader", Pattern.compile("NFReader")),

	/**
	 * NG
	 */
	NG("NG", Pattern.compile("NG")),

	/**
	 * NG-Search
	 */
	NG_SEARCH("NG-Search", Pattern.compile("NG-Search")),

	/**
	 * Nigma.ru
	 */
	NIGMA_RU("Nigma.ru", Pattern.compile("Nigma.ru")),

	/**
	 * NimbleCrawler
	 */
	NIMBLECRAWLER("NimbleCrawler", Pattern.compile("NimbleCrawler")),

	/**
	 * NineSky
	 */
	NINESKY("NineSky", Pattern.compile("NineSky")),

	/**
	 * Nintendo Browser
	 */
	NINTENDO("Nintendo Browser", Pattern.compile("Nintendo Browser")),

	/**
	 * nodestackbot
	 */
	NODESTACKBOT("nodestackbot", Pattern.compile("nodestackbot")),

	/**
	 * Nokia SyncML Client
	 */
	NOKIA_SYNCML_CLIENT("Nokia SyncML Client", Pattern.compile("Nokia SyncML Client")),

	/**
	 * Nokia Web Browser
	 */
	NOKIA_WEB_BROWSER("Nokia Web Browser", Pattern.compile("Nokia Web Browser")),

	/**
	 * Novell BorderManager
	 */
	NOVELL_BORDERMANAGER("Novell BorderManager", Pattern.compile("Novell BorderManager")),

	/**
	 * noyona
	 */
	NOYONA("noyona", Pattern.compile("noyona")),

	/**
	 * NPBot
	 */
	NPBOT("NPBot", Pattern.compile("NPBot")),

	/**
	 * Nuhk
	 */
	NUHK("Nuhk", Pattern.compile("Nuhk")),

	/**
	 * NuSearch Spider
	 */
	NUSEARCH_SPIDER("NuSearch Spider", Pattern.compile("NuSearch Spider")),

	/**
	 * Nutch
	 */
	NUTCH("Nutch", Pattern.compile("Nutch")),

	/**
	 * nworm
	 */
	NWORM("nworm", Pattern.compile("nworm")),

	/**
	 * Nymesis
	 */
	NYMESIS("Nymesis", Pattern.compile("Nymesis")),

	/**
	 * Obigo
	 */
	OBIGO("Obigo", Pattern.compile("Obigo")),

	/**
	 * oBot
	 */
	OBOT("oBot", Pattern.compile("oBot")),

	/**
	 * Ocelli
	 */
	OCELLI("Ocelli", Pattern.compile("Ocelli")),

	/**
	 * Off By One
	 */
	OFF_BY_ONE("Off By One", Pattern.compile("Off By One")),

	/**
	 * Offline Explorer
	 */
	OFFLINE_EXPLORER("Offline Explorer", Pattern.compile("Offline Explorer")),

	/**
	 * Omea Reader
	 */
	OMEA_READER("Omea Reader", Pattern.compile("Omea Reader")),

	/**
	 * OmniExplorer_Bot
	 */
	OMNIEXPLORER_BOT("OmniExplorer_Bot", Pattern.compile("OmniExplorer_Bot")),

	/**
	 * OmniWeb
	 */
	OMNIWEB("OmniWeb", Pattern.compile("OmniWeb")),

	/**
	 * OnetSzukaj
	 */
	ONETSZUKAJ("OnetSzukaj", Pattern.compile("OnetSzukaj")),

	/**
	 * Openbot
	 */
	OPENBOT("Openbot", Pattern.compile("Openbot")),

	/**
	 * OpenCalaisSemanticProxy
	 */
	OPENCALAISSEMANTICPROXY("OpenCalaisSemanticProxy", Pattern.compile("OpenCalaisSemanticProxy")),

	/**
	 * OpenindexSpider
	 */
	OPENINDEXSPIDER("OpenindexSpider", Pattern.compile("OpenindexSpider")),

	/**
	 * Openwave Mobile Browser
	 */
	OPENWAVE_MOBILE_BROWSER("Openwave Mobile Browser", Pattern.compile("Openwave Mobile Browser")),

	/**
	 * Opera
	 */
	OPERA("Opera", Pattern.compile("Opera")),

	/**
	 * Opera Mini
	 */
	OPERA_MINI("Opera Mini", Pattern.compile("Opera Mini")),

	/**
	 * Opera Mobile
	 */
	OPERA_MOBILE("Opera Mobile", Pattern.compile("Opera Mobile")),

	/**
	 * Orbiter
	 */
	ORBITER("Orbiter", Pattern.compile("Orbiter")),

	/**
	 * Orca
	 */
	ORCA("Orca", Pattern.compile("Orca")),

	/**
	 * Oregano
	 */
	OREGANO("Oregano", Pattern.compile("Oregano")),

	/**
	 * OrgbyBot
	 */
	ORGBYBOT("OrgbyBot", Pattern.compile("OrgbyBot")),

	/**
	 * OsObot
	 */
	OSOBOT("OsObot", Pattern.compile("OsObot")),

	/**
	 * Outlook 2007
	 */
	OUTLOOK_2007("Outlook 2007", Pattern.compile("Outlook 2007")),

	/**
	 * Outlook 2010
	 */
	OUTLOOK_2010("Outlook 2010", Pattern.compile("Outlook 2010")),

	/**
	 * Outlook 2013
	 */
	OUTLOOK_2013("Outlook 2013", Pattern.compile("Outlook 2013")),

	/**
	 * OWB
	 */
	OWB("OWB", Pattern.compile("OWB")),

	/**
	 * owsBot
	 */
	OWSBOT("owsBot", Pattern.compile("owsBot")),

	/**
	 * P3P Validator
	 */
	P3P_VALIDATOR("P3P Validator", Pattern.compile("P3P Validator")),

	/**
	 * page_verifier
	 */
	PAGE_VERIFIER("page_verifier", Pattern.compile("page_verifier")),

	/**
	 * Page2RSS
	 */
	PAGE2RSS("Page2RSS", Pattern.compile("Page2RSS")),

	/**
	 * PageBitesHyperBot
	 */
	PAGEBITESHYPERBOT("PageBitesHyperBot", Pattern.compile("PageBitesHyperBot")),

	/**
	 * PagePeeker
	 */
	PAGEPEEKER("PagePeeker", Pattern.compile("PagePeeker")),

	/**
	 * Pale Moon
	 */
	PALE_MOON("Pale Moon", Pattern.compile("Pale Moon")),

	/**
	 * Palm Pre web browser
	 */
	PALM_PRE_WEB_BROWSER("Palm Pre web browser", Pattern.compile("Palm Pre web browser")),

	/**
	 * Panscient web crawler
	 */
	PANSCIENT_WEB_CRAWLER("panscient.com", Pattern.compile("panscient.com")),

	/**
	 * Paparazzi!
	 */
	PAPARAZZI("Paparazzi!", Pattern.compile("Paparazzi!")),

	/**
	 * PaperLiBot
	 */
	PAPERLIBOT("PaperLiBot", Pattern.compile("PaperLiBot")),

	/**
	 * ParchBot
	 */
	PARCHBOT("ParchBot", Pattern.compile("ParchBot")),

	/**
	 * Patriott
	 */
	PATRIOTT("Patriott", Pattern.compile("Patriott")),

	/**
	 * Pattern is a web mining module for the Python programming language.
	 */
	PATTERN("Pattern", Pattern.compile("Pattern")),

	/**
	 * PEAR HTTP_Request
	 */
	PEAR_HTTP_REQUEST("PEAR HTTP_Request", Pattern.compile("PEAR HTTP_Request")),

	/**
	 * Peew
	 */
	PEEW("Peew", Pattern.compile("Peew")),

	/**
	 * percbotspider
	 */
	PERCBOTSPIDER("percbotspider", Pattern.compile("percbotspider")),

	/**
	 * Phaseout
	 */
	PHASEOUT("Phaseout", Pattern.compile("Phaseout")),

	/**
	 * Phoenix (old name for Firefox)
	 */
	PHOENIX("Phoenix (old name for Firefox)", Pattern.compile("Phoenix \\(old name for Firefox\\)")),

	/**
	 * PHP
	 */
	PHP("PHP", Pattern.compile("PHP")),

	/**
	 * PHP link checker
	 */
	PHP_LINK_CHECKER("PHP link checker", Pattern.compile("PHP link checker")),

	/**
	 * PHP OpenID library
	 */
	PHP_OPENID_LIBRARY("PHP OpenID library", Pattern.compile("PHP OpenID library")),

	/**
	 * PHPcrawl
	 */
	PHPCRAWL("PHPcrawl", Pattern.compile("PHPcrawl")),

	/**
	 * pingdom.com_bot
	 */
	PINGDOM_COM_BOT("pingdom.com_bot", Pattern.compile("pingdom.com_bot")),

	/**
	 * Pixray-Seeker
	 */
	PIXRAY_SEEKER("Pixray-Seeker", Pattern.compile("Pixray-Seeker")),

	/**
	 * Plex Media Center
	 */
	PLEX_MEDIA_CENTER("Plex Media Center", Pattern.compile("Plex Media Center")),

	/**
	 * Plukkie
	 */
	PLUKKIE("Plukkie", Pattern.compile("Plukkie")),

	/**
	 * Pocket Tunes
	 */
	POCKET_TUNES("Pocket Tunes", Pattern.compile("Pocket Tunes")),

	/**
	 * PocoMail
	 */
	POCOMAIL("PocoMail", Pattern.compile("PocoMail")),

	/**
	 * Podkicker
	 */
	PODKICKER("Podkicker", Pattern.compile("Podkicker")),

	/**
	 * POE-Component-Client-HTTP
	 */
	POE_COMPONENT_CLIENT_HTTP("POE-Component-Client-HTTP", Pattern.compile("POE-Component-Client-HTTP")),

	/**
	 * Pogodak.co.yu
	 */
	POGODAK_CO_YU("Pogodak.co.yu", Pattern.compile("Pogodak.co.yu")),

	/**
	 * Polaris
	 */
	POLARIS("Polaris", Pattern.compile("Polaris")),

	/**
	 * polixea.de
	 */
	POLIXEA_DE("polixea.de", Pattern.compile("polixea.de")),

	/**
	 * Pompos
	 */
	POMPOS("Pompos", Pattern.compile("Pompos")),

	/**
	 * Postbox
	 */
	POSTBOX("Postbox", Pattern.compile("Postbox")),

	/**
	 * posterus
	 */
	POSTERUS("posterus", Pattern.compile("posterus")),

	/**
	 * PostPost
	 */
	POSTPOST("PostPost", Pattern.compile("PostPost")),

	/**
	 * Powermarks
	 */
	POWERMARKS("Powermarks", Pattern.compile("Powermarks")),

	/**
	 * Prism
	 */
	PRISM("Prism", Pattern.compile("Prism")),

	/**
	 * ProCogBot
	 */
	PROCOGBOT("ProCogBot", Pattern.compile("ProCogBot")),

	/**
	 * proximic
	 */
	PROXIMIC("proximic", Pattern.compile("proximic")),

	/**
	 * PRTG Network Monitor
	 */
	PRTG_NETWORK_MONITOR("PRTG Network Monitor", Pattern.compile("PRTG Network Monitor")),

	/**
	 * PS Vita browser
	 */
	PS_VITA_BROWSER("PS Vita browser", Pattern.compile("PS Vita browser")),

	/**
	 * psbot
	 */
	PSBOT("psbot", Pattern.compile("psbot")),

	/**
	 * ptd-crawler
	 */
	PTD_CRAWLER("ptd-crawler", Pattern.compile("ptd-crawler")),

	/**
	 * Public Radio Player
	 */
	PUBLIC_RADIO_PLAYER("Public Radio Player", Pattern.compile("Public Radio Player")),

	/**
	 * PycURL
	 */
	PYCURL("PycURL", Pattern.compile("PycURL")),

	/**
	 * Python-requests
	 */
	PYTHON_REQUESTS("Python-requests", Pattern.compile("Python-requests")),

	/**
	 * Python-urllib
	 */
	PYTHON_URLLIB("Python-urllib", Pattern.compile("Python-urllib")),

	/**
	 * Python-webchecker
	 */
	PYTHON_WEBCHECKER("Python-webchecker", Pattern.compile("Python-webchecker")),

	/**
	 * Qirina Hurdler
	 */
	QIRINA_HURDLER("Qirina Hurdler", Pattern.compile("Qirina Hurdler")),

	/**
	 * QQbrowser
	 */
	QQBROWSER("QQbrowser", Pattern.compile("QQbrowser")),

	/**
	 * Qseero
	 */
	QSEERO("Qseero", Pattern.compile("Qseero")),

	/**
	 * QtWeb
	 */
	QTWEB("QtWeb", Pattern.compile("QtWeb")),

	/**
	 * Qualidator.com Bot
	 */
	QUALIDATOR_COM_BOT("Qualidator.com Bot", Pattern.compile("Qualidator.com Bot")),

	/**
	 * Quantcastbot
	 */
	QUANTCASTBOT("Quantcastbot", Pattern.compile("Quantcastbot")),

	/**
	 * quickobot
	 */
	QUICKOBOT("quickobot", Pattern.compile("quickobot")),

	/**
	 * QuickTime
	 */
	QUICKTIME("QuickTime", Pattern.compile("QuickTime")),

	/**
	 * QupZilla
	 */
	QUPZILLA("QupZilla", Pattern.compile("QupZilla")),

	/**
	 * R6 bot
	 */
	R6_BOT("R6 bot", Pattern.compile("R6 bot")),

	/**
	 * RADaR-Bot
	 */
	RADAR_BOT("RADaR-Bot", Pattern.compile("RADaR-Bot")),

	/**
	 * Radio Downloader
	 */
	RADIO_DOWNLOADER("Radio Downloader", Pattern.compile("Radio Downloader")),

	/**
	 * RankurBot
	 */
	RANKURBOT("RankurBot", Pattern.compile("RankurBot")),

	/**
	 * RedBot
	 */
	REDBOT("RedBot", Pattern.compile("RedBot")),

	/**
	 * Reeder
	 */
	REEDER("Reeder", Pattern.compile("Reeder")),

	/**
	 * Rekonq
	 */
	REKONQ("Rekonq", Pattern.compile("Rekonq")),

	/**
	 * REL Link Checker Lite
	 */
	REL_LINK_CHECKER_LITE("REL Link Checker Lite", Pattern.compile("REL Link Checker Lite")),

	/**
	 * retawq
	 */
	RETAWQ("retawq", Pattern.compile("retawq")),

	/**
	 * Robo Crawler
	 */
	ROBO_CRAWLER("Robo Crawler", Pattern.compile("Robo Crawler")),

	/**
	 * Robots_Tester
	 */
	ROBOTS_TESTER("Robots_Tester", Pattern.compile("Robots_Tester")),

	/**
	 * Robozilla
	 */
	ROBOZILLA("Robozilla", Pattern.compile("Robozilla")),

	/**
	 * RockMelt
	 */
	ROCKMELT("RockMelt", Pattern.compile("RockMelt")),

	/**
	 * ROME library
	 */
	ROME_LIBRARY("ROME library", Pattern.compile("ROME library")),

	/**
	 * Ronzoobot
	 */
	RONZOOBOT("Ronzoobot", Pattern.compile("Ronzoobot")),

	/**
	 * Rss Bandit
	 */
	RSS_BANDIT("Rss Bandit", Pattern.compile("Rss Bandit")),

	/**
	 * RSS Menu
	 */
	RSS_MENU("RSS Menu", Pattern.compile("RSS Menu")),

	/**
	 * RSS Popper
	 */
	RSS_POPPER("RSS Popper", Pattern.compile("RSS Popper")),

	/**
	 * RSS Radio
	 */
	RSS_RADIO("RSS Radio", Pattern.compile("RSS Radio")),

	/**
	 * RSSMicro.com RSS/Atom Feed Robot
	 */
	RSSMICRO_COM("RSSMicro.com RSS/Atom Feed Robot", Pattern.compile("RSSMicro.com RSS/Atom Feed Robot")),

	/**
	 * RSSOwl
	 */
	RSSOWL("RSSOwl", Pattern.compile("RSSOwl")),

	/**
	 * Ruky-Roboter
	 */
	RUKY_ROBOTER("Ruky-Roboter", Pattern.compile("Ruky-Roboter")),

	/**
	 * Ryouko
	 */
	RYOUKO("Ryouko", Pattern.compile("Ryouko")),

	/**
	 * RyzeCrawler
	 */
	RYZECRAWLER("RyzeCrawler", Pattern.compile("RyzeCrawler")),

	/**
	 * SaaYaa Explorer
	 */
	SAAYAA_EXPLORER("SaaYaa Explorer", Pattern.compile("SaaYaa Explorer")),

	/**
	 * Safari
	 */
	SAFARI("Safari", Pattern.compile("Safari")),

	/**
	 * Safari RSS reader
	 */
	SAFARI_RSS_READER("Safari RSS reader", Pattern.compile("Safari RSS reader")),

	/**
	 * Sage
	 */
	SAGE("Sage", Pattern.compile("Sage")),

	/**
	 * SAI Crawler
	 */
	SAI_CRAWLER("SAI Crawler", Pattern.compile("SAI Crawler")),

	/**
	 * SanszBot
	 */
	SANSZBOT("SanszBot", Pattern.compile("SanszBot")),

	/**
	 * SBIder
	 */
	SBIDER("SBIder", Pattern.compile("SBIder")),

	/**
	 * SBSearch
	 */
	SBSEARCH("SBSearch", Pattern.compile("SBSearch")),

	/**
	 * Scarlett
	 */
	SCARLETT("Scarlett", Pattern.compile("Scarlett")),

	/**
	 * schibstedsokbot
	 */
	SCHIBSTEDSOKBOT("schibstedsokbot", Pattern.compile("schibstedsokbot")),

	/**
	 * ScollSpider
	 */
	SCOLLSPIDER("ScollSpider", Pattern.compile("ScollSpider")),

	/**
	 * Scooter
	 */
	SCOOTER("Scooter", Pattern.compile("Scooter")),

	/**
	 * ScoutJet
	 */
	SCOUTJET("ScoutJet", Pattern.compile("ScoutJet")),

	/**
	 * SeaMonkey
	 */
	SEAMONKEY("SeaMonkey", Pattern.compile("SeaMonkey")),

	/**
	 * Search Engine World Robots.txt Validator
	 */
	SEARCH_ENGINE_WORLD_ROBOTS_TXT_VALIDATOR("Search Engine World Robots.txt Validator", Pattern
			.compile("Search Engine World Robots.txt Validator")),

	/**
	 * search.KumKie.com
	 */
	SEARCH_KUMKIE_COM("search.KumKie.com", Pattern.compile("search.KumKie.com")),

	/**
	 * Search17Bot
	 */
	SEARCH17BOT("Search17Bot", Pattern.compile("Search17Bot")),

	/**
	 * Semager
	 */
	SEMAGER("Semager", Pattern.compile("Semager")),

	/**
	 * SEMC Browser
	 */
	SEMC_BROWSER("SEMC Browser", Pattern.compile("SEMC Browser")),

	/**
	 * SemrushBot
	 */
	SEMRUSHBOT("SemrushBot", Pattern.compile("SemrushBot")),

	/**
	 * Sensis Web Crawler
	 */
	SENSIS_WEB_CRAWLER("Sensis Web Crawler", Pattern.compile("Sensis Web Crawler")),

	/**
	 * SEODat
	 */
	SEODAT("SEODat", Pattern.compile("SEODat")),

	/**
	 * SEOENGBot
	 */
	SEOENGBOT("SEOENGBot", Pattern.compile("SEOENGBot")),

	/**
	 * SEOkicks-Robot
	 */
	SEOKICKS_ROBOT("SEOkicks-Robot", Pattern.compile("SEOkicks-Robot")),

	/**
	 * Setoozbot
	 */
	SETOOZBOT("Setoozbot", Pattern.compile("Setoozbot")),

	/**
	 * Seznam RSS reader
	 */
	SEZNAM_RSS_READER("Seznam RSS reader", Pattern.compile("Seznam RSS reader")),

	/**
	 * Seznam WAP Proxy
	 */
	SEZNAM_WAP_PROXY("Seznam WAP Proxy", Pattern.compile("Seznam WAP Proxy")),

	/**
	 * SeznamBot
	 */
	SEZNAMBOT("SeznamBot", Pattern.compile("SeznamBot")),

	/**
	 * SharpReader
	 */
	SHARPREADER("SharpReader", Pattern.compile("SharpReader")),

	/**
	 * Shelob
	 */
	SHELOB("Shelob", Pattern.compile("Shelob")),

	/**
	 * Shiira
	 */
	SHIIRA("Shiira", Pattern.compile("Shiira")),

	/**
	 * Shim-Crawler
	 */
	SHIM_CRAWLER("Shim-Crawler", Pattern.compile("Shim-Crawler")),

	/**
	 * ShopWiki
	 */
	SHOPWIKI("ShopWiki", Pattern.compile("ShopWiki")),

	/**
	 * ShowyouBot
	 */
	SHOWYOUBOT("ShowyouBot", Pattern.compile("ShowyouBot")),

	/**
	 * Shredder
	 */
	SHREDDER("Shredder", Pattern.compile("Shredder")),

	/**
	 * Siege
	 */
	SIEGE("Siege", Pattern.compile("Siege")),

	/**
	 * silk
	 */
	SILK("Silk", Pattern.compile("silk", Pattern.CASE_INSENSITIVE)),

	/**
	 * SimplePie
	 */
	SIMPLEPIE("SimplePie", Pattern.compile("SimplePie")),

	/**
	 * Sirketce/Busiverse
	 */
	SIRKETCE_BUSIVERSE("Sirketce/Busiverse", Pattern.compile("Sirketce/Busiverse")),

	/**
	 * sistrix
	 */
	SISTRIX("sistrix", Pattern.compile("sistrix")),

	/**
	 * Sitedomain-Bot
	 */
	SITEDOMAIN_BOT("Sitedomain-Bot", Pattern.compile("Sitedomain-Bot")),

	/**
	 * SiteKiosk
	 */
	SITEKIOSK("SiteKiosk", Pattern.compile("SiteKiosk")),

	/**
	 * SiteSucker
	 */
	SITESUCKER("SiteSucker", Pattern.compile("SiteSucker")),

	/**
	 * SkipStone
	 */
	SKIPSTONE("SkipStone", Pattern.compile("SkipStone")),

	/**
	 * SkreemRBot
	 */
	SKREEMRBOT("SkreemRBot", Pattern.compile("SkreemRBot")),

	/**
	 * Skyfire
	 */
	SKYFIRE("Skyfire", Pattern.compile("Skyfire")),

	/**
	 * Sleipnir
	 */
	SLEIPNIR("Sleipnir", Pattern.compile("Sleipnir")),

	/**
	 * SlimBoat
	 */
	SLIMBOAT("SlimBoat", Pattern.compile("SlimBoat")),

	/**
	 * SlimBrowser
	 */
	SLIMBROWSER("SlimBrowser", Pattern.compile("SlimBrowser")),

	/**
	 * smart.apnoti.com Robot
	 */
	SMART_APNOTI_COM_ROBOT("smart.apnoti.com Robot", Pattern.compile("smart.apnoti.com Robot")),

	/**
	 * snap.com
	 */
	SNAP_COM("snap.com", Pattern.compile("snap.com")),

	/**
	 * SnapBot
	 */
	SNAPBOT("SnapBot", Pattern.compile("SnapBot")),

	/**
	 * Snappy
	 */
	SNAPPY("Snappy", Pattern.compile("Snappy")),

	/**
	 * SniffRSS
	 */
	SNIFFRSS("SniffRSS", Pattern.compile("SniffRSS")),

	/**
	 * Snoopy
	 */
	SNOOPY("Snoopy", Pattern.compile("Snoopy")),

	/**
	 * Sogou
	 */
	SOGOU("Sogou", Pattern.compile("Sogou")),

	/**
	 * Sogou Explorer
	 */
	SOGOU_EXPLORER("Sogou Explorer", Pattern.compile("Sogou Explorer")),

	/**
	 * sogou spider
	 */
	SOGOU_SPIDER("sogou spider", Pattern.compile("sogou spider")),

	/**
	 * Songbird
	 */
	SONGBIRD("Songbird", Pattern.compile("Songbird")),

	/**
	 * Sosospider
	 */
	SOSOSPIDER("Sosospider", Pattern.compile("Sosospider")),

	/**
	 * Sparrow
	 */
	SPARROW("Sparrow", Pattern.compile("Sparrow")),

	/**
	 * spbot
	 */
	SPBOT("spbot", Pattern.compile("spbot")),

	/**
	 * Speedy
	 */
	SPEEDY("Speedy Spider", Pattern.compile("Speedy Spider")),

	/**
	 * Spicebird
	 */
	SPICEBIRD("Spicebird", Pattern.compile("Spicebird")),

	/**
	 * SpiderLing
	 */
	SPIDERLING("SpiderLing", Pattern.compile("SpiderLing")),

	/**
	 * Spinn3r
	 */
	SPINN3R("Spinn3r", Pattern.compile("Spinn3r")),

	/**
	 * Spock Crawler
	 */
	SPOCK_CRAWLER("Spock Crawler", Pattern.compile("Spock Crawler")),

	/**
	 * SpokeSpider
	 */
	SPOKESPIDER("SpokeSpider", Pattern.compile("SpokeSpider")),

	/**
	 * Sproose
	 */
	SPROOSE("Sproose", Pattern.compile("Sproose")),

	/**
	 * SrevBot
	 */
	SREVBOT("SrevBot", Pattern.compile("SrevBot")),

	/**
	 * SSLBot
	 */
	SSLBOT("SSLBot", Pattern.compile("SSLBot")),

	/**
	 * StackRambler
	 */
	STACKRAMBLER("StackRambler", Pattern.compile("StackRambler")),

	/**
	 * Stainless
	 */
	STAINLESS("Stainless", Pattern.compile("Stainless")),

	/**
	 * StatoolsBot
	 */
	STATOOLSBOT("StatoolsBot", Pattern.compile("StatoolsBot")),

	/**
	 * Steeler
	 */
	STEELER("Steeler", Pattern.compile("Steeler")),

	/**
	 * Strokebot
	 */
	STROKEBOT("Strokebot", Pattern.compile("Strokebot")),

	/**
	 * SubStream
	 */
	SUBSTREAM("SubStream", Pattern.compile("SubStream")),

	/**
	 * suggybot
	 */
	SUGGYBOT("suggybot", Pattern.compile("suggybot")),

	/**
	 * Summer
	 */
	SUMMER("Summer", Pattern.compile("Summer")),

	/**
	 * Sundance
	 */
	SUNDANCE("Sundance", Pattern.compile("Sundance")),

	/**
	 * Sundial
	 */
	SUNDIAL("Sundial", Pattern.compile("Sundial")),

	/**
	 * Sunrise
	 */
	SUNRISE("Sunrise", Pattern.compile("Sunrise")),

	/**
	 * SuperBot
	 */
	SUPERBOT("SuperBot", Pattern.compile("SuperBot")),

	/**
	 * Surf
	 */
	SURF("Surf", Pattern.compile("Surf")),

	/**
	 * Surphace Scout
	 */
	SURPHACE_SCOUT("Surphace Scout", Pattern.compile("Surphace Scout")),

	/**
	 * SurveyBot
	 */
	SURVEYBOT("SurveyBot", Pattern.compile("SurveyBot")),

	/**
	 * SWEBot
	 */
	SWEBOT("SWEBot", Pattern.compile("SWEBot")),

	/**
	 * Swiftfox
	 */
	SWIFTFOX("Swiftfox", Pattern.compile("Swiftfox")),

	/**
	 * Swiftweasel
	 */
	SWIFTWEASEL("Swiftweasel", Pattern.compile("Swiftweasel")),

	/**
	 * SygolBot
	 */
	SYGOLBOT("SygolBot", Pattern.compile("SygolBot")),

	/**
	 * SynooBot
	 */
	SYNOOBOT("SynooBot", Pattern.compile("SynooBot")),

	/**
	 * Szukacz
	 */
	SZUKACZ("Szukacz", Pattern.compile("Szukacz")),

	/**
	 * Szukankobot
	 */
	SZUKANKOBOT("Szukankobot", Pattern.compile("Szukankobot")),

	/**
	 * Tagoobot
	 */
	TAGOOBOT("Tagoobot", Pattern.compile("Tagoobot")),

	/**
	 * taptubot
	 */
	TAPTUBOT("taptubot", Pattern.compile("taptubot")),

	/**
	 * Tear
	 */
	TEAR("Tear", Pattern.compile("Tear")),

	/**
	 * TeaShark
	 */
	TEASHARK("TeaShark", Pattern.compile("TeaShark")),

	/**
	 * Technoratibot
	 */
	TECHNORATIBOT("Technoratibot", Pattern.compile("Technoratibot")),

	/**
	 * Teleport Pro
	 */
	TELEPORT_PRO("Teleport Pro", Pattern.compile("Teleport Pro")),

	/**
	 * TenFourFox
	 */
	TENFOURFOX("TenFourFox", Pattern.compile("TenFourFox")),

	/**
	 * TeragramCrawler
	 */
	TERAGRAMCRAWLER("TeragramCrawler", Pattern.compile("TeragramCrawler")),

	/**
	 * textractor
	 */
	TEXTRACTOR("textractor", Pattern.compile("textractor")),

	/**
	 * The Bat!
	 */
	THE_BAT("The Bat!", Pattern.compile("The Bat!")),

	/**
	 * Theophrastus
	 */
	THEOPHRASTUS("Theophrastus", Pattern.compile("Theophrastus")),

	/**
	 * TheWorld Browser
	 */
	THEWORLD_BROWSER("TheWorld Browser", Pattern.compile("TheWorld Browser")),

	/**
	 * Thumbnail.CZ robot
	 */
	THUMBNAIL_CZ_ROBOT("Thumbnail.CZ robot", Pattern.compile("Thumbnail.CZ robot")),

	/**
	 * ThumbShots-Bot
	 */
	THUMBSHOTS_BOT("ThumbShots-Bot", Pattern.compile("ThumbShots-Bot")),

	/**
	 * thumbshots-de-Bot
	 */
	THUMBSHOTS_DE_BOT("thumbshots-de-Bot", Pattern.compile("thumbshots-de-Bot")),

	/**
	 * Thumbshots.ru
	 */
	THUMBSHOTS_RU("Thumbshots.ru", Pattern.compile("Thumbshots.ru")),

	/**
	 * Thunderbird
	 */
	THUNDERBIRD("Thunderbird", Pattern.compile("Thunderbird")),

	/**
	 * TinEye
	 */
	TINEYE("TinEye", Pattern.compile("TinEye")),

	/**
	 * Tizen Browser
	 */
	TIZEN_BROWSER("Tizen Browser", Pattern.compile("Tizen Browser")),

	/**
	 * Tjusig
	 */
	TJUSIG("Tjusig", Pattern.compile("Tjusig")),

	/**
	 * Topicbot
	 */
	TOPICBOT("Topicbot", Pattern.compile("Topicbot")),

	/**
	 * Toread-Crawler
	 */
	TOREAD_CRAWLER("Toread-Crawler", Pattern.compile("Toread-Crawler")),

	/**
	 * Touche
	 */
	TOUCHE("Touche", Pattern.compile("Touche")),

	/**
	 * trendictionbot
	 */
	TRENDICTIONBOT("trendictionbot", Pattern.compile("trendictionbot")),

	/**
	 * Trileet NewsRoom
	 */
	TRILEET_NEWSROOM("Trileet NewsRoom", Pattern.compile("Trileet NewsRoom")),

	/**
	 * TT Explorer
	 */
	TT_EXPLORER("TT Explorer", Pattern.compile("TT Explorer")),

	/**
	 * Tulip Chain
	 */
	TULIP_CHAIN("Tulip Chain", Pattern.compile("Tulip Chain")),

	/**
	 * TurnitinBot
	 */
	TURNITINBOT("TurnitinBot", Pattern.compile("TurnitinBot")),

	/**
	 * TutorGigBot
	 */
	TUTORGIGBOT("TutorGigBot", Pattern.compile("TutorGigBot")),

	/**
	 * TwengaBot
	 */
	TWENGABOT("TwengaBot", Pattern.compile("TwengaBot")),

	/**
	 * Twiceler
	 */
	TWICELER("Twiceler", Pattern.compile("Twiceler")),

	/**
	 * Twikle
	 */
	TWIKLE("Twikle", Pattern.compile("Twikle")),

	/**
	 * Typhoeus
	 */
	TYPHOEUS("Typhoeus", Pattern.compile("Typhoeus")),

	/**
	 * UASlinkChecker
	 */
	UASLINKCHECKER("UASlinkChecker", Pattern.compile("UASlinkChecker")),

	/**
	 * UC Browser
	 */
	UC_BROWSER("UC Browser", Pattern.compile("UC Browser")),

	/**
	 * UltraBrowser
	 */
	ULTRABROWSER("UltraBrowser ", Pattern.compile("UltraBrowser ")),

	/**
	 * UnisterBot
	 */
	UNISTERBOT("UnisterBot", Pattern.compile("UnisterBot")),

	/**
	 * UnwindFetchor
	 */
	UNWINDFETCHOR("UnwindFetchor", Pattern.compile("UnwindFetchor")),

	/**
	 * updated
	 */
	UPDATED("updated", Pattern.compile("updated")),

	/**
	 * Updownerbot
	 */
	UPDOWNERBOT("Updownerbot", Pattern.compile("Updownerbot")),

	/**
	 * UptimeDog
	 */
	UPTIMEDOG("UptimeDog", Pattern.compile("UptimeDog")),

	/**
	 * UptimeRobot
	 */
	UPTIMEROBOT("UptimeRobot", Pattern.compile("UptimeRobot")),

	/**
	 * urlfan-bot
	 */
	URLFAN_BOT("urlfan-bot", Pattern.compile("urlfan-bot")),

	/**
	 * Urlfilebot (Urlbot)
	 */
	URLFILEBOT("Urlfilebot (Urlbot)", Pattern.compile("Urlfilebot \\(Urlbot\\)")),

	/**
	 * urlgrabber
	 */
	URLGRABBER("urlgrabber", Pattern.compile("urlgrabber")),

	/**
	 * Usejump
	 */
	USEJUMP("Usejump", Pattern.compile("Usejump")),

	/**
	 * uZard Web
	 */
	UZARD_WEB("uZard Web", Pattern.compile("uZard Web")),

	/**
	 * Uzbl
	 */
	UZBL("Uzbl", Pattern.compile("Uzbl")),

	/**
	 * Vagabondo
	 */
	VAGABONDO("Vagabondo", Pattern.compile("Vagabondo")),

	/**
	 * Validator.nu
	 */
	VALIDATOR_NU("Validator.nu", Pattern.compile("Validator.nu")),

	/**
	 * VERASYS 2k
	 */
	VERASYS_2K("VERASYS 2k", Pattern.compile("VERASYS 2k")),

	/**
	 * Vermut
	 */
	VERMUT("Vermut", Pattern.compile("Vermut")),

	/**
	 * Vespa Crawler
	 */
	VESPA_CRAWLER("Vespa Crawler", Pattern.compile("Vespa Crawler")),

	/**
	 * VideoSurf_bot
	 */
	VIDEOSURF_BOT("VideoSurf_bot", Pattern.compile("VideoSurf_bot")),

	/**
	 * virus_detector
	 */
	VIRUS_DETECTOR("virus_detector", Pattern.compile("virus_detector")),

	/**
	 * Vivaldi
	 */
	VIVALDI("Vivaldi", Pattern.compile("Vivaldi")),

	/**
	 * Visbot
	 */
	VISBOT("Visbot", Pattern.compile("Visbot")),

	/**
	 * VLC media player
	 */
	VLC_MEDIA_PLAYER("VLC media player", Pattern.compile("VLC media player")),

	/**
	 * VMBot
	 */
	VMBOT("VMBot", Pattern.compile("VMBot")),

	/**
	 * void-bot
	 */
	VOID_BOT("void-bot", Pattern.compile("void-bot")),

	/**
	 * VoilaBot
	 */
	VOILABOT("OrangeBot", Pattern.compile("OrangeBot")),

	/**
	 * Vonkeror
	 */
	VONKEROR("Vonkeror", Pattern.compile("Vonkeror")),

	/**
	 * VORTEX
	 */
	VORTEX("VORTEX", Pattern.compile("VORTEX")),

	/**
	 * voyager
	 */
	VOYAGER("voyager", Pattern.compile("voyager")),

	/**
	 * Vuze
	 */
	VUZE("Vuze", Pattern.compile("Vuze")),

	/**
	 * VWBot
	 */
	VWBOT("VWBot", Pattern.compile("VWBot")),

	/**
	 * W3C Checklink
	 */
	W3C_CHECKLINK("W3C Checklink", Pattern.compile("W3C Checklink")),

	/**
	 * W3C CSS Validator
	 */
	W3C_CSS_VALIDATOR("W3C CSS Validator", Pattern.compile("W3C CSS Validator")),

	/**
	 * W3C mobileOK Checker
	 */
	W3C_MOBILEOK_CHECKER("W3C mobileOK Checker", Pattern.compile("W3C mobileOK Checker")),

	/**
	 * W3C Validator
	 */
	W3C_VALIDATOR("W3C Validator", Pattern.compile("W3C Validator")),

	/**
	 * w3m
	 */
	W3M("w3m", Pattern.compile("w3m")),

	/**
	 * WapTiger
	 */
	WAPTIGER("WapTiger", Pattern.compile("WapTiger")),

	/**
	 * WASALive-Bot
	 */
	WASALIVE_BOT("WASALive-Bot", Pattern.compile("WASALive-Bot")),

	/**
	 * WatchMouse
	 */
	WATCHMOUSE("WatchMouse", Pattern.compile("WatchMouse")),

	/**
	 * WBSearchBot
	 */
	WBSEARCHBOT("WBSearchBot", Pattern.compile("WBSearchBot")),

	/**
	 * WDG CSSCheck
	 */
	WDG_CSSCHECK("WDG CSSCheck", Pattern.compile("WDG CSSCheck")),

	/**
	 * WDG Page Valet
	 */
	WDG_PAGE_VALET("WDG Page Valet", Pattern.compile("Page Valet")),

	/**
	 * WDG Validator
	 */
	WDG_VALIDATOR("WDG Validator", Pattern.compile("WDG Validator")),

	/**
	 * Web-sniffer
	 */
	WEB_SNIFFER("Web-sniffer", Pattern.compile("Web-sniffer")),

	/**
	 * WebAlta Crawler
	 */
	WEBALTA_CRAWLER("WebAlta Crawler", Pattern.compile("WebAlta Crawler")),

	/**
	 * WebarooBot
	 */
	WEBAROOBOT("WebarooBot", Pattern.compile("WebarooBot")),

	/**
	 * WebCollage
	 */
	WEBCOLLAGE("WebCollage", Pattern.compile("WebCollage")),

	/**
	 * WebCopier
	 */
	WEBCOPIER("WebCopier", Pattern.compile("WebCopier")),

	/**
	 * webfetch
	 */
	WEBFETCH("webfetch", Pattern.compile("webfetch")),

	/**
	 * webfs
	 */
	WEBFS("webfs", Pattern.compile("webfs")),

	/**
	 * Webian Shell
	 */
	WEBIAN_SHELL("Webian Shell", Pattern.compile("Webian Shell")),

	/**
	 * WebImages
	 */
	WEBIMAGES("WebImages", Pattern.compile("WebImages")),

	/**
	 * webinatorbot
	 */
	WEBINATORBOT("webinatorbot", Pattern.compile("webinatorbot")),

	/**
	 * webmastercoffee
	 */
	WEBMASTERCOFFEE("webmastercoffee", Pattern.compile("webmastercoffee")),

	/**
	 * WebNL
	 */
	WEBNL("WebNL", Pattern.compile("WebNL")),

	/**
	 * WebRankSpider
	 */
	WEBRANKSPIDER("WebRankSpider", Pattern.compile("WebRankSpider")),

	/**
	 * WebRender
	 */
	WEBRENDER("WebRender", Pattern.compile("WebRender")),

	/**
	 * Webscope Crawler
	 */
	WEBSCOPE_CRAWLER("Webscope Crawler", Pattern.compile("Webscope Crawler")),

	/**
	 * WebStripper
	 */
	WEBSTRIPPER("WebStripper", Pattern.compile("WebStripper")),

	/**
	 * WebWatch/Robot_txtChecker
	 */
	WEBWATCH_ROBOT_TXT_CHECKER("WebWatch/Robot_txtChecker", Pattern.compile("WebWatch/Robot_txtChecker")),

	/**
	 * WebZIP
	 */
	WEBZIP("WebZIP", Pattern.compile("WebZIP")),

	/**
	 * wectar
	 */
	WECTAR("wectar", Pattern.compile("wectar")),

	/**
	 * Weltweitimnetz Browser
	 */
	WELTWEITIMNETZ_BROWSER("Weltweitimnetz Browser", Pattern.compile("Weltweitimnetz Browser")),

	/**
	 * WeSEE:Search
	 */
	WESEE_SEARCH("WeSEE:Search", Pattern.compile("WeSEE:Search")),

	/**
	 * Wget
	 */
	WGET("Wget", Pattern.compile("Wget")),

	/**
	 * Whoismindbot
	 */
	WHOISMINDBOT("Whoismindbot", Pattern.compile("Whoismindbot")),

	/**
	 * WikioFeedBot
	 */
	WIKIOFEEDBOT("WikioFeedBot", Pattern.compile("WikioFeedBot")),

	/**
	 * wikiwix-bot
	 */
	WIKIWIX_BOT("wikiwix-bot", Pattern.compile("wikiwix-bot")),

	/**
	 * Willow Internet Crawler
	 */
	WILLOW_INTERNET_CRAWLER("Willow Internet Crawler", Pattern.compile("Willow Internet Crawler")),

	/**
	 * Winamp for Android
	 */
	WINAMP_FOR_ANDROID("Winamp for Android", Pattern.compile("Winamp for Android")),

	/**
	 * Windows Live Mail
	 */
	WINDOWS_LIVE_MAIL("Windows Live Mail", Pattern.compile("Windows Live Mail")),

	/**
	 * Windows Media Player
	 */
	WINDOWS_MEDIA_PLAYER("Windows Media Player", Pattern.compile("Windows Media Player")),

	/**
	 * WinHTTP
	 */
	WINHTTP("WinHTTP", Pattern.compile("WinHTTP")),

	/**
	 * WinkBot
	 */
	WINKBOT("WinkBot", Pattern.compile("WinkBot")),

	/**
	 * WinPodder
	 */
	WINPODDER("WinPodder", Pattern.compile("WinPodder")),

	/**
	 * WinWap
	 */
	WINWAP("WinWap", Pattern.compile("WinWap")),

	/**
	 * WinWebBot
	 */
	WINWEBBOT("WinWebBot", Pattern.compile("WinWebBot")),

	/**
	 * WIRE
	 */
	WIRE("WIRE", Pattern.compile("WIRE")),

	/**
	 * wKiosk
	 */
	WKIOSK("wKiosk", Pattern.compile("wKiosk")),

	/**
	 * WMCAI_robot
	 */
	WMCAI_ROBOT("WMCAI_robot", Pattern.compile("WMCAI_robot")),

	/**
	 * Woko
	 */
	WOKO("Woko", Pattern.compile("Woko")),

	/**
	 * WordPress pingback
	 */
	WORDPRESS_PINGBACK("WordPress pingback", Pattern.compile("WordPress pingback")),

	/**
	 * woriobot
	 */
	WORIOBOT("woriobot", Pattern.compile("woriobot")),

	/**
	 * WorldWideWeb
	 */
	WORLDWIDEWEB("WorldWideWeb", Pattern.compile("WorldWideWeb")),

	/**
	 * wOSBrowser
	 */
	WOSBROWSER("wOSBrowser", Pattern.compile("wOSBrowser")),

	/**
	 * Wotbox
	 */
	WOTBOX("Wotbox", Pattern.compile("Wotbox")),

	/**
	 * wsAnalyzer
	 */
	WSANALYZER("wsAnalyzer", Pattern.compile("wsAnalyzer")),

	/**
	 * www.fi crawler
	 */
	WWW_FI_CRAWLER("www.fi crawler", Pattern.compile("www.fi crawler")),

	/**
	 * WWW::Mechanize
	 */
	WWW_MECHANIZE("WWW::Mechanize", Pattern.compile("WWW::Mechanize")),

	/**
	 * wwwster
	 */
	WWWSTER("wwwster", Pattern.compile("wwwster")),

	/**
	 * Wyzo
	 */
	WYZO("Wyzo", Pattern.compile("Wyzo")),

	/**
	 * X-Smiles
	 */
	X_SMILES("X-Smiles", Pattern.compile("X-Smiles")),

	/**
	 * Xaldon WebSpider
	 */
	XALDON_WEBSPIDER("Xaldon WebSpider", Pattern.compile("Xaldon WebSpider")),

	/**
	 * XBMC
	 */
	XBMC("XBMC", Pattern.compile("XBMC")),

	/**
	 * Xenu
	 */
	XENU("Xenu", Pattern.compile("Xenu")),

	/**
	 * xine
	 */
	XINE("xine", Pattern.compile("xine")),

	/**
	 * XmarksFetch
	 */
	XMARKSFETCH("XmarksFetch", Pattern.compile("XmarksFetch")),

	/**
	 * XML-RPC for PHP
	 */
	XML_RPC_FOR_PHP("XML-RPC for PHP", Pattern.compile("XML-RPC for PHP")),

	/**
	 * XML-RPC for Ruby
	 */
	XML_RPC_FOR_RUBY("XML-RPC for Ruby", Pattern.compile("XML-RPC for Ruby")),

	/**
	 * XML Sitemaps Generator
	 */
	XML_SITEMAPS_GENERATOR("XML Sitemaps Generator", Pattern.compile("XML Sitemaps Generator")),

	/**
	 * XMPlay
	 */
	XMPLAY("XMPlay", Pattern.compile("XMPlay")),

	/**
	 * Yaanb
	 */
	YAANB("Yaanb", Pattern.compile("Yaanb")),

	/**
	 * yacybot
	 */
	YACYBOT("yacybot", Pattern.compile("yacybot")),

	/**
	 * Yahoo!
	 */
	YAHOO("Yahoo!", Pattern.compile("Yahoo!")),

	/**
	 * Yahoo Link Preview
	 */
	YAHOO_LINK_PREVIEW("Yahoo Link Preview", Pattern.compile("Yahoo Link Preview")),

	/**
	 * Yahoo! JAPAN
	 */
	YAHOO_JAPAN("Yahoo! JAPAN", Pattern.compile("Yahoo! JAPAN")),

	/**
	 * YahooFeedSeeker
	 */
	YAHOOFEEDSEEKER("YahooFeedSeeker", Pattern.compile("YahooFeedSeeker")),

	/**
	 * Yandex.Browser
	 */
	YANDEX_BROWSER("Yandex.Browser", Pattern.compile("Yandex\\.Browser")),

	/**
	 * YandexBot
	 */
	YANDEXBOT("YandexBot", Pattern.compile("YandexBot")),

	/**
	 * Yanga
	 */
	YANGA("Yanga", Pattern.compile("Yanga")),

	/**
	 * YeahReader
	 */
	YEAHREADER("YeahReader", Pattern.compile("YeahReader")),

	/**
	 * YioopBot
	 */
	YIOOPBOT("YioopBot", Pattern.compile("YioopBot")),

	/**
	 * YodaoBot
	 */
	YODAOBOT("YodaoBot", Pattern.compile("YodaoBot")),

	/**
	 * Yoono Bot
	 */
	YOONO_BOT("Yoono Bot", Pattern.compile("Yoono Bot")),

	/**
	 * YoudaoBot
	 */
	YOUDAOBOT("YoudaoBot", Pattern.compile("YoudaoBot")),

	/**
	 * YowedoBot
	 */
	YOWEDOBOT("YowedoBot", Pattern.compile("YowedoBot")),

	/**
	 * YRSpider
	 */
	YRSPIDER("YRSpider", Pattern.compile("YRSpider")),

	/**
	 * ZACATEK_CZ
	 */
	ZACATEK_CZ("ZACATEK_CZ", Pattern.compile("ZACATEK_CZ")),

	/**
	 * zBrowser
	 */
	ZBROWSER("zBrowser", Pattern.compile("zBrowser")),

	/**
	 * Zend_Http_Client
	 */
	ZEND_HTTP_CLIENT("Zend_Http_Client", Pattern.compile("Zend_Http_Client")),

	/**
	 * Zeusbot
	 */
	ZEUSBOT("Zeusbot", Pattern.compile("Zeusbot")),

	/**
	 * ZipZap
	 */
	ZIPZAP("ZipZap", Pattern.compile("ZipZap")),

	/**
	 * ZookaBot
	 */
	ZOOKABOT("ZookaBot", Pattern.compile("ZookaBot(/\\d+(\\.\\d+)*)?", Pattern.CASE_INSENSITIVE)),

	/**
	 * ZoomSpider (ZSEBOT)
	 */
	ZOOMSPIDER("ZoomSpider (ZSEBOT)", Pattern.compile("ZoomSpider \\(ZSEBOT\\)")),

	/**
	 * ZyBorg
	 */
	ZYBORG("ZyBorg", Pattern.compile("ZyBorg"));

	/**
	 * This method try to find by the given family name a matching enum value. The family name must match against an
	 * user agent entry in UAS data file.
	 * 
	 * @param family
	 *            name of an user agent family
	 * @return the matching enum value or {@code UserAgentFamily#UNKNOWN}
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if the given argument is {@code null}
	 */
	@Nonnull
	public static UserAgentFamily evaluate(@Nonnull final String family) {
		Check.notNull(family, "family");

		UserAgentFamily result = UNKNOWN;

		// search by name
		result = evaluateByName(family);

		// search by pattern
		if (result == UNKNOWN) {
			result = evaluateByPattern(family);
		}

		return result;
	}

	/**
	 * This method try to find by the given family name a matching enum value. The family name will be evaluated against
	 * the stored name of an user agent entry.
	 * 
	 * @param family
	 *            name of an user agent family
	 * @return the matching enum value or {@code UserAgentFamily#UNKNOWN}
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if the given argument is {@code null}
	 */
	@Nonnull
	protected static UserAgentFamily evaluateByName(@Nonnull final String family) {
		Check.notNull(family, "family");

		UserAgentFamily result = UNKNOWN;
		for (final UserAgentFamily value : values()) {
			if (value.getName().equalsIgnoreCase(family)) {
				result = value;
				break;
			}
		}

		return result;
	}

	/**
	 * This method try to find by the given family name a matching enum value. The family name will be evaluated against
	 * the stored regular expression of an user agent entry.
	 * 
	 * @param family
	 *            name of an user agent family
	 * @return the matching enum value or {@code UserAgentFamily#UNKNOWN}
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if the given argument is {@code null}
	 */
	@Nonnull
	protected static UserAgentFamily evaluateByPattern(@Nonnull final String family) {
		Check.notNull(family, "family");

		UserAgentFamily result = UNKNOWN;
		for (final UserAgentFamily value : values()) {
			final Matcher m = value.getPattern().matcher(family);
			if (m.matches()) {
				result = value;
				break;
			}
		}

		return result;
	}

	/**
	 * The internal family name in the UAS database.
	 */
	@Nonnull
	private final String name;

	/**
	 * The regular expression which a family name must be match.
	 */
	@Nonnull
	private final Pattern pattern;

	private UserAgentFamily(@Nonnull final String name, @Nonnull final Pattern pattern) {
		this.name = name;
		this.pattern = pattern;
	}

	/**
	 * Gets the internal family name in the UAS database.
	 * 
	 * @return the internal family name
	 */
	@Nonnull
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the regular expression which a family name must be match with.
	 * 
	 * @return regular expression
	 */
	@Nonnull
	public Pattern getPattern() {
		return pattern;
	}

}
