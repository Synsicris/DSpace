<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1"
								xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
								xmlns:fo="http://www.w3.org/1999/XSL/Format"
								xmlns:cerif="https://www.openaire.eu/cerif-profile/1.1/"
								exclude-result-prefixes="fo">

<!--########################################################################-->
<!-- LANGUAGE TRANSLATION -->	
<!--########################################################################-->

<!-- sections -->	
<xsl:param name="lang.title.section1" select="'1 Ziele'"/>
<xsl:param name="lang.title.section1.1" select="'1.1 Gesamtziel des Vorhabens'"/>
<xsl:param name="lang.title.section1.2" select="'1.2 Wissenschaftliche und/oder technische Arbeitsziele des Vorhabens'"/>
<xsl:param name="lang.title.section1.3" select="'1.3 Arbeitsziele zur Interaktion'"/>
<xsl:param name="lang.title.section1.4" select="'1.4 Bezug des Vorhabens zu den förderpolitischen Zielen'"/>

<!-- TODO: add all sections -->

<!-- individual fields -->	
<xsl:param name="lang.term" select="'Laufzeit'"/>
<xsl:param name="lang.duration.month" select="'Dauer in Monaten'"/>

<xsl:param name="lang.funding.call" select="'Förderbereich/Bekanntmachung'"/>
<xsl:param name="lang.funding.programme" select="'Fördermaßnahme/Förderprogramm'"/>

<xsl:param name="lang.postcode-city-country" select="'PLZ / Stadt / Land'"/>
<xsl:param name="lang.website" select="'Webadresse'"/>
<xsl:param name="lang.organisation.form" select="'Organisationsform'"/>
<xsl:param name="lang.cooperation.type" select="'Art der Kooperation'"/>

<xsl:param name="lang.organisation.field-of-activity" select="'Tätigkeitsbereich der Organisation'"/>
<xsl:param name="lang.organisation.nace" select="'Wirtschaftszweig der Organisation'"/>
<xsl:param name="lang.organisation.destatis" select="'Disziplin / Fachgebiet der Organisation'"/>
<xsl:param name="lang.organisation.political-level" select="'Politikebenen der Organisation'"/>
<xsl:param name="lang.organisation.project-contribution" select="'Beschreibung der Zusammenarbeit'"/>			
<xsl:param name="lang.organisation.size" select="'Unternehmensgröße'"/>
<xsl:param name="lang.organisation.location-type" select="'Verortung der Akteursgruppe'"/>			

<xsl:param name="lang.targetgroup.relevance" select="'Relevanz der Akteursgruppe für das Vorhaben'"/>			

<!-- TODO: add all fields -->

<!--########################################################################-->
<!-- VARIABLES -->	
<!--########################################################################-->

<!-- CURRENT PROBLEMS AND QUESTIONS -->	
<!-- so far relative paths are not correctly handled, that is why absolute paths are given -->

<!-- paths (NOTE: path needs to be given with single hyphens otherwise the path is interpreted as XPath element)-->	
<xsl:param name="imageDirectory" select="'/opt/dspace/dspace-syn7/install/config/crosswalks/template/images/'"/>


<!-- approach in the following: characteristic first, effected element last -->

<!-- font sizes -->	
<xsl:param name="font.size.title" select="'24pt'"/>
<xsl:param name="font.size.section-title" select="'14pt'"/>
<xsl:param name="font.size.sub-section-title" select="'12pt'"/>
<xsl:param name="font.size.sub-sub-section-title" select="'11pt'"/>
<xsl:param name="font.size.standard" select="'10pt'"/>
<xsl:param name="font.size.key-value" select="'13pt'"/> 

<!-- font weigths -->
<xsl:param name="font.weight.title" select="'bold'"/>
<xsl:param name="font.weight.key" select="'bold'"/>
<xsl:param name="font.weight.value" select="'normal'"/> 
<xsl:param name="font.weight.key-value" select="'bold'"/> 

<!-- alignments -->
<xsl:param name="text.alignment.title" select="'left'"/>
<xsl:param name="text.alignment.key" select="'left'"/>
<xsl:param name="text.alignment.value" select="'left'"/>

<!-- margins -->	
<xsl:param name="margin.left.text" select="'3mm'"/>
<xsl:param name="margin.top.title" select="'5mm'"/>	
<xsl:param name="margin.top.main" select="'2mm'"/>
<xsl:param name="margin.top.section" select="'10mm'"/>
<xsl:param name="margin.bottom.section" select="'-3mm'"/>
<xsl:param name="margin.left.border" select="'3mm'"/>
<xsl:param name="margin.right.border" select="'3mm'"/>
<xsl:param name="margin.top.gap" select="'5mm'"/>

<!-- paddings -->	
<xsl:param name="padding.before.border" select="'2mm'"/>
<xsl:param name="padding.after.border" select="'2mm'"/>
<xsl:param name="padding.start.border" select="'3mm'"/>
<xsl:param name="padding.end.border" select="'3mm'"/>

<!-- lengths -->
<xsl:param name="length.ruler" select="'100%'"/>

<!-- widths -->
<xsl:param name="width.border" select="'0.3mm'"/>
<xsl:param name="width.table" select="'97%'"/>

<!-- colours  -->
<xsl:param name="colour.border" select="'#808080'"/>

<!-- styles -->
<xsl:param name="style.border" select="'solid'"/>

<!--########################################################################-->
<!-- MAIN PAGE -->	
<!--########################################################################-->

	<xsl:template match="cerif:Project">	
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

    <!--==========================================================--> 
		<!-- LAYOUT MASTER SET-->
    <!--==========================================================--> 

			<fo:layout-master-set>
				<!-- page properties -->
				<fo:simple-page-master master-name="A4-process-events"
															 page-height="29.7cm" 
															 page-width="24cm" 
															 margin-top="2cm"
															 margin-bottom="2cm" 
															 margin-left="1cm" 
															 margin-right="1cm">

					<!-- properties of the body region -->																 
					<fo:region-body></fo:region-body>	

					<!-- properties of header -->
					<fo:region-before></fo:region-before>

					<!-- properties of footer -->

					<fo:region-after></fo:region-after>

					<!-- properties of the left side -->
					<fo:region-start></fo:region-start>

					<!-- properties of the right side -->
					<fo:region-end></fo:region-end>					

				</fo:simple-page-master>
			</fo:layout-master-set>

    <!--==========================================================--> 
		<!-- PAGE SEQUENCE -->
    <!--==========================================================--> 

			<fo:page-sequence master-reference="A4-process-events"
			                  initial-page-number="1">

				<!-- integrate page numbers -->
				<fo:static-content flow-name="xsl-region-after">
     			<fo:block text-align="center">
       			Seite <fo:page-number/>
     			</fo:block>
   			</fo:static-content>

				<!-- start page flow -->
				<fo:flow flow-name="xsl-region-body">

          <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
					<!-- SECTION #0 --> 
          <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->

				  <!-- project title -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="cerif:Title"/>
						<xsl:with-param name="fontSize" select="$font.size.title"/>
					</xsl:call-template>

				  <!-- section title - basics -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'Vorhabensbeschreibung / Basisbericht'"/>
						<xsl:with-param name="fontSize" select="$font.size.section-title"/>
					</xsl:call-template>

					<!-- contents -->
					<xsl:call-template name="project"/>
					<xsl:call-template name="project-partner"/>

          <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
					<!-- SECTION #1 --> 
          <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->

					<!-- new page -->
          <fo:block break-after='page'/>

				  <!-- section title - aims -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section1"/>
						<xsl:with-param name="fontSize" select="$font.size.section-title"/>
					</xsl:call-template>

				  <!-- sub section title - overall aim of the project -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section1.1"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-section-title"/>
					</xsl:call-template>

					<xsl:call-template name="impact-pathway"/>

				  <!-- sub section title - scientific and techical aims of the project -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section1.2"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-section-title"/>
					</xsl:call-template>					

					<xsl:call-template name="project-objective"/>

				  <!-- sub section title - work aims regarding interaction -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section1.3"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-section-title"/>
					</xsl:call-template>	

					<xsl:call-template name="interaction-objective"/>

				  <!-- sub section title - relation of the project to the objectives of the funding policy  -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section1.4"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-section-title"/>
					</xsl:call-template>	
			
					<xsl:call-template name="contribution-funding-programme"/>

          <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
					<!-- SECTION #2 --> 
          <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->

					<!-- new page -->
          <fo:block break-after='page'/>

          <!-- section title - background -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'2 Hintergrund'"/>
						<xsl:with-param name="fontSize" select="$font.size.section-title"/>
					</xsl:call-template>

				  <!-- sub section title - state of the art in science and technology / previous works -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'2.1	Stand der Wissenschaft und Technik / Bisherige Arbeiten'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-section-title"/>
					</xsl:call-template>

					<xsl:call-template name="state-of-art"/>

          <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
					<!-- SECTION #3 --> 
          <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->

					<!-- new page -->
          <fo:block break-after='page'/>

          <!-- section title - working plan -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'3	Ausführliche Beschreibung des Arbeitsplans'"/>
						<xsl:with-param name="fontSize" select="$font.size.section-title"/>
					</xsl:call-template>

				  <!-- sub section title - resource planning of the project -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'3.1	Vorhabenbezogene Ressourcenplanung'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-section-title"/>
					</xsl:call-template>

				  <!-- sub sub section title - overview working plan -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'3.1.1	Überblick Arbeitsplanung'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>
			
					<xsl:call-template name="working-plan-overview"/>
					
				  <!-- sub sub section title - work packages and tasks -->					
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'3.1.2	Arbeitspakete und Arbeiten'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

					<xsl:call-template name="work-package"/>

				  <!-- sub sub section title - research work -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'3.1.3	Forschungsarbeiten'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

				  <!-- sub sub section title - interaction and transfer -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'3.1.4	Arbeiten zur Interaktion und Transfer'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

				  <!-- sub sub section title - cooperation partners and target groups that are addressed -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'3.1.5 Kooperationspartner und Akteursgruppen, die adressiert werden'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

				  <!-- sub sub section title - overview interaction and transfer -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'3.1.6 Arbeiten zur Interaktion und Transfer'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

          <xsl:call-template name="interaction-transfer-overview"/>

				  <!-- sub section title - milestone planning -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'3.2	Meilensteinplan'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-section-title"/>
					</xsl:call-template>

					<xsl:call-template name="milestone"/>

				  <!-- sub section title - in-depth material and methods -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'3.3	Vertiefung Material und Methoden'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-section-title"/>
					</xsl:call-template>

          <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
					<!-- SECTION #4 --> 
          <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
 
					<!-- new page -->
          <fo:block break-after='page'/>

          <!-- section title - exploitation and impact -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'4	Verwertung und Wirkung'"/>
						<xsl:with-param name="fontSize" select="$font.size.section-title"/>
					</xsl:call-template>

				  <!-- sub section title - overview -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'4.1	Überblick'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-section-title"/>
					</xsl:call-template>

				  <!-- sub section title - details of exploitation and impact of the project -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'4.2	Details zu Verwertung und Wirkung des Projektes'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-section-title"/>
					</xsl:call-template>

				  <!-- sub section title - application possibilities -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'4.3	Anwendung(smöglichkeiten)'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-section-title"/>
					</xsl:call-template>

				  <!-- sub sub section title - solutions/changes/innovations -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'4.3.1	Lösung/Veränderung/Innovation'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

					<!-- sub sub section title - ideas for solutions/changes/innovations  -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'4.3.2	Ideen für eine Lösung/Veränderung/Innovation'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

					<!-- sub sub section title - application  -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'4.3.3	Anwendung'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

					<!-- <xsl:call-template name="application"/> -->

					<!-- sub sub section title - unexpected results -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'4.3.4	Unerwartete Erkentnisse und Probleme'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

          <!-- <xsl:call-template name="unexpected-result"/> -->

					<!-- sub sub section title - patents -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'4.3.5 Patente'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

          <!-- <xsl:call-template name="patent"/> -->

					<!-- sub sub section title - spinoffs -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'4.3.6 Ausgründungen'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

        	  <xsl:call-template name="spinoff"/> 

					<!-- sub sub section title - work and cooperation after the project -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'4.3.7 Arbeiten und Kooperationen nach Projektende'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

					<!-- sub sub section title - awards -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'4.3.8 Auszeichnungen und Preise'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

					<xsl:call-template name="award"/>

					<!-- sub sub section title - open research questions  -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'4.3.9 Offenen Forschungsfragen'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

					<xsl:call-template name="open-research-question"/>

				  <!-- sub section title - social impact and reflections of the project -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'4.4	Gesellschaftliche Wirkungen und Reflexionen zum Projekt'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-section-title"/>
					</xsl:call-template>

				  <!-- sub sub section title - condition  -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'4.4.1	Rahmenbedingungen'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

					<xsl:call-template name="condition"/>

				  <!-- sub sub section title - expected societal impact -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'4.4.2	Gesellschaftliche Wirkung'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

 					<xsl:call-template name="expected-societal-impact"/>

				  <!-- sub sub section title - description of potential negative side effects -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'4.4.3	Negative Auswirkungs'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

					<xsl:call-template name="negative-side-effect"/>

				  <!-- sub sub section title - description of ethical aspects -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'4.4.4	Ethische Aspekte'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

					<xsl:call-template name="ethics"/>

				  <!-- sub sub section title - description of gender aspects  -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'4.4.5	Gender'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

		      <xsl:call-template name="gender"/>

				  <!-- sub section title - exploitation plans of the individual project partners -->
					<!-- NOTE: this section is currently not needed -->

					<!-- <xsl:call-template name="title">
						<xsl:with-param name="title" select="'4.5	Verwertungspläne der einzelnen Projektpartner'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-section-title"/>
					</xsl:call-template>					 -->

          <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
					<!-- SECTION #5 --> 
          <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->

					<!-- new page -->
          <fo:block break-after='page'/>

          <!-- section title - work distribution/collaboration with third parties -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'5	Arbeitsteilung/Zusammenarbeit mit Dritten'"/>
						<xsl:with-param name="fontSize" select="$font.size.section-title"/>
					</xsl:call-template>

				  <!-- sub section title - project partners (additional informations) -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'5.1	Projektpartner'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-section-title"/>
					</xsl:call-template>		

				  <!-- sub section title - cooperation partners -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'5.2	Kooperationspartner'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-section-title"/>
					</xsl:call-template>		

					<xsl:call-template name="cooperation-partner"/>

				  <!-- sub section title - relevant stakeholder groups of the project -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'5.3	Relevante Akteursgruppen des Projektes'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-section-title"/>
					</xsl:call-template>

          <xsl:call-template name="target-group"/>

          <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
					<!-- SECTION #6 --> 
          <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
s
					<!-- new page -->
          <fo:block break-after='page'/>

          <!-- section title - necessity of the grant -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'6	Notwendigkeit der Zuwendung'"/>
						<xsl:with-param name="fontSize" select="$font.size.section-title"/>
					</xsl:call-template>

          <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
					<!-- SECTION FOR TESTING --> 
          <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->

					<!-- new page -->

<!--
          <fo:block break-after='page'/>

					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'Testbereich'"/>
						<xsl:with-param name="fontSize" select="$font.size.section-title"/>
					</xsl:call-template>

					<xsl:call-template name="title">
						<xsl:with-param name="title" select="cerif:Test"/>
						<xsl:with-param name="fontSize" select="$font.size.title"/>
					</xsl:call-template>

					<xsl:call-template name="key-value-line-list"> 
						<xsl:with-param name="key" select="'Test'"/>
						<xsl:with-param name="value" select="cerif:WorkPackage/cerif:Index/cerif:Task/cerif:Index/cerif:Title"/>
				  </xsl:call-template>
-->	

				</fo:flow>

			</fo:page-sequence>

		</fo:root>
	</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE PROJECT ENTITY -->	
<!--########################################################################-->

 <!-- TODO: adjust indentation of all code -->

	<xsl:template name="project" >

		<xsl:call-template name="key-value-single">
			<xsl:with-param name="key" select="'Akronym'"/>
			<xsl:with-param name="value" select="cerif:Acronym"/>
		</xsl:call-template>

		<xsl:call-template name="key-period">
			<xsl:with-param name="key" select="$lang.term"/>
			<xsl:with-param name="startdate" select="cerif:StartDate"/>
			<xsl:with-param name="enddate" select="cerif:EndDate"/>
		</xsl:call-template>
	
		<xsl:call-template name="key-value-single">
			<xsl:with-param name="key" select="$lang.duration.month"/>
			<xsl:with-param name="value" select="cerif:Duration"/>
		</xsl:call-template>

		<xsl:call-template name="key-value-single">
			<xsl:with-param name="key" select="$lang.funding.call"/>
			<xsl:with-param name="value" select="cerif:FundingCall"/>
		</xsl:call-template>

		<xsl:call-template name="key-value-single">
			<xsl:with-param name="key" select="$lang.funding.programme"/>
			<xsl:with-param name="value" select="cerif:FundingProgramme"/>
		</xsl:call-template>			
	
	</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE PROJECT PARTNER ENTITY -->	
<!--########################################################################-->

	<xsl:template name="project-partner">

		<xsl:if test="cerif:ProjectPartner">

			<xsl:call-template name="vertical-gap">  <!-- make a small gap first to indicate a separation from the items above-->
				<xsl:with-param name="margin" select="$margin.top.gap"/>
		  </xsl:call-template>

			<xsl:call-template name="key-only">  <!-- partner key -->
				<xsl:with-param name="key" select="'Partner'"/>
		  </xsl:call-template>	

			<xsl:for-each select="cerif:ProjectPartner/cerif:Index">

				<fo:block border-width="{$width.border}"
									border-color="{$colour.border}"
									border-style="{$style.border}"
									margin-top="{$margin.top.main}"
									margin-left="{$margin.left.border}"
									margin-right="{$margin.right.border}"
									padding-before="{$padding.before.border}"
									padding-after="{$padding.after.border}"
									padding-start="{$padding.start.border}"
									padding-end="{$padding.end.border}">
									
					<xsl:call-template name="value-single">
						<xsl:with-param name="value" select="cerif:Acronym"/>
						<xsl:with-param name="fontSize" select="$font.size.key-value"/>
						<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
					</xsl:call-template>									

					<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="'Ausführende Stelle im easy-online-Antrag'"/>
						<xsl:with-param name="value" select="cerif:EasyOnlineImport"/>
				  </xsl:call-template>						

					<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="'Tätigkeitsbereich der Organisationseinheit'"/>
						<xsl:with-param name="value" select="cerif:FieldOfActivity"/>
				  </xsl:call-template>						

					<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="'Disziplin / Fachgebiet der Forschungseinrichtung'"/>
						<xsl:with-param name="value" select="cerif:Destatis"/>
				  </xsl:call-template>	
					
					<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="'Wirtschaftszweig der Organisation'"/>
						<xsl:with-param name="value" select="cerif:NACE"/>
				  </xsl:call-template>	
					
					<xsl:call-template name="key-value-comma-list">
						<xsl:with-param name="key" select="'Politikebene(n) der Organisationseinheit'"/>
						<xsl:with-param name="value" select="cerif:PoliticalLevel"/>
				  </xsl:call-template>						

					<xsl:call-template name="vertical-gap">
						<xsl:with-param name="margin" select="$margin.top.gap"/>
					</xsl:call-template>

					<xsl:call-template name="key-value-single"> <!-- TODO: not shown -->
						<xsl:with-param name="key" select="'Übergeordnete Organisation'"/>
						<xsl:with-param name="value" select="cerif:Import/cerif:ParentOrganisation"/>
				  </xsl:call-template>	

					<xsl:call-template name="key-value-single"> 
						<xsl:with-param name="key" select="'Name der ausführenden Stelle / Organisationseinheit'"/>
						<xsl:with-param name="value" select="cerif:Import/cerif:OrganisationName"/>
				  </xsl:call-template>					

					<xsl:call-template name="key-postcode-city-country">
						<xsl:with-param name="key" select="$lang.postcode-city-country"/>
						<xsl:with-param name="postcode" select="cerif:Import/cerif:PostCode"/>
						<xsl:with-param name="city" select="cerif:Import/cerif:City"/>
						<xsl:with-param name="country" select="cerif:Import/cerif:Country"/>
				  </xsl:call-template>	

					<xsl:call-template name="key-value-comma-list"> 
						<xsl:with-param name="key" select="'Webadresse'"/>
						<xsl:with-param name="value" select="cerif:Import/cerif:WebAddress"/>
				  </xsl:call-template>	

					<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="'Organisationsform'"/>
						<xsl:with-param name="value" select="cerif:Import/cerif:OrganisationType"/>
				  </xsl:call-template>						

					<xsl:call-template name="vertical-gap">
						<xsl:with-param name="margin" select="$margin.top.gap"/>
					</xsl:call-template>

					<xsl:call-template name="key-name-degree-gender">
						<xsl:with-param name="key" select="'Projektleitung'"/>
						<xsl:with-param name="name" select="cerif:ImportLead/cerif:Name"/>
						<xsl:with-param name="degree" select="cerif:ImportLead/cerif:Degree"/>
						<xsl:with-param name="gender" select="cerif:ImportLead/cerif:Gender"/>
				  </xsl:call-template>	

				</fo:block> 		

			</xsl:for-each> 

	  </xsl:if>

	</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE IMPACT PATHWAY -->	
<!--########################################################################-->

  <xsl:template name="impact-pathway" >

	  <xsl:if test="cerif:ImpactPathway">

		  <xsl:for-each select="cerif:ImpactPathway/cerif:Index">

			  <xsl:call-template name="title-section-counter"> 
				<xsl:with-param name="section" select="'1.1.'"/>				
				  <xsl:with-param name="counter" select="position()"/>
					<xsl:with-param name="title" select="' Beschreibung des Gesamtziels des Vorhabens über den Impact Pathway'"/>
					<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
			  </xsl:call-template>			

				<xsl:call-template name="value-single">
					<xsl:with-param name="value" select="cerif:Description"/>
					<xsl:with-param name="fontSize" select="$font.size.standard"/>
					<xsl:with-param name="fontWeight" select="$font.weight.value"/>
				</xsl:call-template>

		  </xsl:for-each>

	  </xsl:if>
		
  </xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE PROJECT OBJECTIVE ENTITY -->	
<!--########################################################################-->

  <xsl:template name="project-objective">

		<xsl:call-template name="table-title-description">
			<xsl:with-param name="entity" select="cerif:ProjectObjective"/>
			<xsl:with-param name="entityIndex" select="cerif:ProjectObjective/cerif:Index"/>
		</xsl:call-template>	

	</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE INTERACTION OBJECTIVE ENTITY -->	
<!--########################################################################-->

  <xsl:template name="interaction-objective">

		<xsl:call-template name="table-title-description">
			<xsl:with-param name="entity" select="cerif:InteractionObjective"/>
			<xsl:with-param name="entityIndex" select="cerif:InteractionObjective/cerif:Index"/>
		</xsl:call-template>	

  </xsl:template>	

<!--########################################################################-->
<!-- TEMPLATE FOR THE CONTRIBUTION FUNDING PROGRAMME ENTITY -->	
<!--########################################################################-->

  <xsl:template name="contribution-funding-programme">

		<xsl:call-template name="table-title-description">
			<xsl:with-param name="entity" select="cerif:ContributionFundingProgramme"/>
			<xsl:with-param name="entityIndex" select="cerif:ContributionFundingProgramme/cerif:Index"/>
		</xsl:call-template>	

  </xsl:template>	

<!--########################################################################-->
<!-- TEMPLATE FOR THE STATE OF ART/SCIENCE ENTITY -->	
<!--########################################################################-->

  <xsl:template name="state-of-art">

		<xsl:if test="cerif:StateOfArt">

			<xsl:for-each select="cerif:StateOfArt/cerif:Index">

				<fo:block border-width="{$width.border}"
									border-color="{$colour.border}"
									border-style="{$style.border}"
									margin-top="{$margin.top.main}"
									margin-left="{$margin.left.border}"
									margin-right="{$margin.right.border}"
									padding-before="{$padding.before.border}"
									padding-after="{$padding.after.border}"
									padding-start="{$padding.start.border}"
									padding-end="{$padding.end.border}">

						<xsl:call-template name="value-single">
							<xsl:with-param name="value" select="cerif:Title"/>
							<xsl:with-param name="fontSize" select="$font.size.key-value"/>
							<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
						</xsl:call-template>	

						<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="'Beschreibung'"/>
						<xsl:with-param name="value" select="cerif:Description"/>
					</xsl:call-template>

			  </fo:block> 		

			</xsl:for-each> -->
	
		</xsl:if>
	
	</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE INTERACTION TRANSFER OVERVIEW -->	
<!--########################################################################-->

  <xsl:template name="interaction-transfer-overview">
	
		<fo:block	margin-top="{$margin.top.main}"
							padding-before="{$padding.before.border}"
							padding-after="{$padding.after.border}"
							padding-start="{$padding.start.border}"
							padding-end="{$padding.end.border}">
	
			<fo:table table-layout="fixed" vertical-align="middle">
	
				<!-- define the table columns -->
				<fo:table-column column-width="proportional-column-width(1)"/>	<!-- this construct is used to align the table in a centred way -->							
				<fo:table-column column-width="'19.4%'"/>
				<fo:table-column column-width="'19.4%'"/>
				<fo:table-column column-width="'19.4%'"/>
				<fo:table-column column-width="'19.4%'"/>
				<fo:table-column column-width="'19.4%'"/>
				<fo:table-column column-width="proportional-column-width(1)"/>  <!-- this is also part of central alignment approach --> 
																																			  <!-- in addition the columns need to be numbered, omitting the first and last column --> 
				<!-- table header -->
				<fo:table-header>
					<fo:table-cell column-number="2" 
												 border-width="${width.border}"
												 border-color="{$colour.border}"
												 border-style="{$style.border}">
						<fo:block text-align="center" 
											font-size="{$font.size.key-value}"
											font-weight="{$font.weight.key-value}"
											margin-top="{$margin.top.main}"
											padding-before="{$padding.before.border}"
											padding-after="{$padding.after.border}"
											padding-start="{$padding.start.border}"
											padding-end="{$padding.end.border}"
											margin-left="{$margin.left.border}"
											margin-right="{$margin.right.border}">	

							<xsl:variable name="imageFile" select="concat('file:',$imageDirectory,'event.png')" />

							<fo:external-graphic content-width="12mm">  <!-- content-height="scale-to-fit" scaling="non-uniform" -->
								<xsl:attribute name="src">
									<xsl:value-of select="$imageFile" />
								</xsl:attribute>
							</fo:external-graphic>

							<fo:inline vertical-align="top">
							   <xsl:text>    </xsl:text> 
							  <xsl:value-of select="count(cerif:Event/cerif:Index)" />
              </fo:inline>

						</fo:block>
					</fo:table-cell>

					<fo:table-cell column-number="3" 
												 border-width="${width.border}"
												 border-color="{$colour.border}"
												 border-style="{$style.border}">
						<fo:block text-align="center" 
											font-size="{$font.size.key-value}"
											font-weight="{$font.weight.key-value}"
											margin-top="{$margin.top.main}"
											padding-before="{$padding.before.border}"
											padding-after="{$padding.after.border}"
											padding-start="{$padding.start.border}"
											padding-end="{$padding.end.border}"
											margin-left="{$margin.left.border}"
											margin-right="{$margin.right.border}">	

							<xsl:variable name="imageFile" select="concat('file:',$imageDirectory,'process.png')" />

							<fo:external-graphic content-width="12mm">  <!-- content-height="scale-to-fit" scaling="non-uniform" -->
								<xsl:attribute name="src">
									<xsl:value-of select="$imageFile" />
								</xsl:attribute>
							</fo:external-graphic>

							<fo:inline vertical-align="top">
							  <xsl:text>    </xsl:text> 
							  <xsl:value-of select="count(cerif:ProcessEvent/cerif:Index)" />
              </fo:inline>

						</fo:block>
					</fo:table-cell>

					<fo:table-cell column-number="4" 
												 border-width="${width.border}"
												 border-color="{$colour.border}"
												 border-style="{$style.border}">
						<fo:block text-align="center" 
											font-size="{$font.size.key-value}"
											font-weight="{$font.weight.key-value}"
											margin-top="{$margin.top.main}"
											padding-before="{$padding.before.border}"
											padding-after="{$padding.after.border}"
											padding-start="{$padding.start.border}"
											padding-end="{$padding.end.border}"
											margin-left="{$margin.left.border}"
											margin-right="{$margin.right.border}">	

							<xsl:variable name="imageFile" select="concat('file:',$imageDirectory,'publication.png')" />

							<fo:external-graphic content-width="12mm">  <!-- content-height="scale-to-fit" scaling="non-uniform" -->
								<xsl:attribute name="src">
									<xsl:value-of select="$imageFile" />
								</xsl:attribute>
							</fo:external-graphic>

							<fo:inline vertical-align="top">
							  <xsl:text>    </xsl:text> 
							  <xsl:value-of select="count(cerif:Publication/cerif:Index)" />
              </fo:inline>

						</fo:block>
					</fo:table-cell>

					<fo:table-cell column-number="5" 
												 border-width="${width.border}"
												 border-color="{$colour.border}"
												 border-style="{$style.border}">
						<fo:block text-align="center" 
											font-size="{$font.size.key-value}"
											font-weight="{$font.weight.key-value}"
											margin-top="{$margin.top.main}"
											padding-before="{$padding.before.border}"
											padding-after="{$padding.after.border}"
											padding-start="{$padding.start.border}"
											padding-end="{$padding.end.border}"
											margin-left="{$margin.left.border}"
											margin-right="{$margin.right.border}">	

							<xsl:variable name="imageFile" select="concat('file:',$imageDirectory,'object-material.png')" />

							<fo:external-graphic content-width="12mm">  <!-- content-height="scale-to-fit" scaling="non-uniform" -->
								<xsl:attribute name="src">
									<xsl:value-of select="$imageFile" />
								</xsl:attribute>
							</fo:external-graphic>

							<fo:inline vertical-align="top">
							  <xsl:text>    </xsl:text> 
							  <xsl:value-of select="count(cerif:Product/cerif:Index)" />
              </fo:inline>

						</fo:block>
					</fo:table-cell>

				</fo:table-header>

				<!-- table body -->
				<fo:table-body>					
						<fo:table-row>

							<fo:table-cell column-number="2"
														border-width="${width.border}"
														border-color="{$colour.border}"
														border-style="{$style.border}">
								<fo:block text-align="center" 
													font-size="{$font.size.standard}"
													font-weight="{$font.weight.value}"
													margin-top="{$margin.top.main}"
													padding-before="{$padding.before.border}"
													padding-after="{$padding.after.border}"
													padding-start="{$padding.start.border}"
													padding-end="{$padding.end.border}"
													margin-left="{$margin.left.border}"
													margin-right="{$margin.right.border}">
									<xsl:text>Events</xsl:text>
								</fo:block>
							</fo:table-cell>

							<fo:table-cell column-number="3"
														border-width="${width.border}"
														border-color="{$colour.border}"
														border-style="{$style.border}">
								<fo:block text-align="center" 
													font-size="{$font.size.standard}"
													font-weight="{$font.weight.value}"
													margin-top="{$margin.top.main}"
													padding-before="{$padding.before.border}"
													padding-after="{$padding.after.border}"
													padding-start="{$padding.start.border}"
													padding-end="{$padding.end.border}"
													margin-left="{$margin.left.border}"
													margin-right="{$margin.right.border}">
									<xsl:text>Kooperations-prozesse</xsl:text>
								</fo:block>
							</fo:table-cell>

							<fo:table-cell column-number="4"
														border-width="${width.border}"
														border-color="{$colour.border}"
														border-style="{$style.border}">
								<fo:block text-align="center" 
													font-size="{$font.size.standard}"
													font-weight="{$font.weight.value}"
													margin-top="{$margin.top.main}"
													padding-before="{$padding.before.border}"
													padding-after="{$padding.after.border}"
													padding-start="{$padding.start.border}"
													padding-end="{$padding.end.border}"
													margin-left="{$margin.left.border}"
													margin-right="{$margin.right.border}">
									<xsl:text>Veröffentlichungen</xsl:text>
								</fo:block>
							</fo:table-cell>

							<fo:table-cell column-number="5"
														border-width="${width.border}"
														border-color="{$colour.border}"
														border-style="{$style.border}">
								<fo:block text-align="center" 
													font-size="{$font.size.standard}"
													font-weight="{$font.weight.value}"
													margin-top="{$margin.top.main}"
													padding-before="{$padding.before.border}"
													padding-after="{$padding.after.border}"
													padding-start="{$padding.start.border}"
													padding-end="{$padding.end.border}"
													margin-left="{$margin.left.border}"
													margin-right="{$margin.right.border}">
									<xsl:text>Objekte/Materialien</xsl:text>
								</fo:block>
							</fo:table-cell>

						</fo:table-row>
				</fo:table-body>				

			</fo:table>								
	
	  </fo:block> 

  </xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE MILESTONES -->	
<!--########################################################################-->

	<xsl:template name="milestone">

		<xsl:if test="cerif:Milestone">

			<xsl:for-each select="cerif:Milestone/cerif:Index">

				<fo:block border-width="{$width.border}"
									border-color="{$colour.border}"
									border-style="{$style.border}"
									margin-top="{$margin.top.main}"
									margin-left="{$margin.left.border}"
									margin-right="{$margin.right.border}"
									padding-before="{$padding.before.border}"
									padding-after="{$padding.after.border}"
									padding-start="{$padding.start.border}"
									padding-end="{$padding.end.border}">

					<xsl:call-template name="value-single">
						<xsl:with-param name="value" select="cerif:Title"/>
						<xsl:with-param name="fontSize" select="$font.size.key-value"/>
						<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
					</xsl:call-template>	

					<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="'Beschreibung'"/>
						<xsl:with-param name="value" select="cerif:Description"/>
					</xsl:call-template>

				</fo:block> 		

			</xsl:for-each>

		</xsl:if>

	</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE WORKING PLAN OVERVIEW -->	
<!--########################################################################-->

<xsl:template name="working-plan-overview">

			<fo:block border-width="{$width.border}"
					border-color="{$colour.border}"
					border-style="{$style.border}"
					margin-top="{$margin.top.main}"
					margin-left="{$margin.left.border}"
					margin-right="{$margin.right.border}"
					padding-before="{$padding.before.border}"
					padding-after="{$padding.after.border}"
					padding-start="{$padding.start.border}"
					padding-end="{$padding.end.border}">
			
					<xsl:call-template name="key-value-line-list" >				
							<xsl:with-param name = "key" select="'Arbeitspakete'"/>
							<xsl:with-param name = "value" select="cerif:WorkPackage/cerif:Index/cerif:Title" />
							<xsl:with-param name="fontSize" select="$font.size.key-value"/>
						<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
					</xsl:call-template>

			</fo:block>

			<fo:block border-width="{$width.border}"
						border-color="{$colour.border}"
						border-style="{$style.border}"
						margin-top="{$margin.top.main}"
						margin-left="{$margin.left.border}"
						margin-right="{$margin.right.border}"
						padding-before="{$padding.before.border}"
						padding-after="{$padding.after.border}"
						padding-start="{$padding.start.border}"
						padding-end="{$padding.end.border}">

					<xsl:call-template name="key-value-line-list" >
							<xsl:with-param name = "key" select="'Aufgaben'"/>
							<xsl:with-param name = "value" select="cerif:Task/cerif:Index/cerif:Title" />
							<xsl:with-param name="fontSize" select="$font.size.key-value"/>
						<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
					</xsl:call-template>

			</fo:block>	

			<fo:block border-width="{$width.border}"
						border-color="{$colour.border}"
						border-style="{$style.border}"
						margin-top="{$margin.top.main}"
						margin-left="{$margin.left.border}"
						margin-right="{$margin.right.border}"
						padding-before="{$padding.before.border}"
						padding-after="{$padding.after.border}"
						padding-start="{$padding.start.border}"
						padding-end="{$padding.end.border}">

					<xsl:call-template name="key-value-line-list" >
						<xsl:with-param name = "key" select="'Veranstaltungen'"/>
						<xsl:with-param name = "value" select="cerif:Event/cerif:Index/cerif:Title" />
						<xsl:with-param name="fontSize" select="$font.size.key-value"/>
					<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
					</xsl:call-template>

			</fo:block>	

			<fo:block border-width="{$width.border}"
						border-color="{$colour.border}"
						border-style="{$style.border}"
						margin-top="{$margin.top.main}"
						margin-left="{$margin.left.border}"
						margin-right="{$margin.right.border}"
						padding-before="{$padding.before.border}"
						padding-after="{$padding.after.border}"
						padding-start="{$padding.start.border}"
						padding-end="{$padding.end.border}">

					<xsl:call-template name="key-value-line-list" >
						<xsl:with-param name = "key" select="'Projekttreffen'"/>
						<xsl:with-param name = "value" select="cerif:ProcessEvent/cerif:Index/cerif:Title" />
						<xsl:with-param name="fontSize" select="$font.size.key-value"/>
					<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
					</xsl:call-template>

				</fo:block>	

				<fo:block border-width="{$width.border}"
							border-color="{$colour.border}"
							border-style="{$style.border}"
							margin-top="{$margin.top.main}"
							margin-left="{$margin.left.border}"
							margin-right="{$margin.right.border}"
							padding-before="{$padding.before.border}"
							padding-after="{$padding.after.border}"
							padding-start="{$padding.start.border}"
							padding-end="{$padding.end.border}">

					<xsl:call-template name="key-value-line-list" >
						<xsl:with-param name = "key" select="'Geplante Veröffentlichungen'"/>
						<xsl:with-param name = "value" select="cerif:PlannedPublication/cerif:Index/cerif:Title" />
						<xsl:with-param name="fontSize" select="$font.size.key-value"/>
					<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
					</xsl:call-template>

				</fo:block>	

				<fo:block border-width="{$width.border}"
							border-color="{$colour.border}"
							border-style="{$style.border}"
							margin-top="{$margin.top.main}"
							margin-left="{$margin.left.border}"
							margin-right="{$margin.right.border}"
							padding-before="{$padding.before.border}"
							padding-after="{$padding.after.border}"
							padding-start="{$padding.start.border}"
							padding-end="{$padding.end.border}">

					<xsl:call-template name="key-value-line-list" >
						<xsl:with-param name = "key" select="'Gegenstände/Materialien'"/>
						<xsl:with-param name = "value" select="cerif:PhysicalObject/cerif:Index/cerif:Title" />
						<xsl:with-param name="fontSize" select="$font.size.key-value"/>
					<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
					</xsl:call-template>

 				</fo:block>	

</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE WORK PACKAGES -->	
<!--########################################################################-->

<xsl:template name="work-package">
				
	<xsl:if test="cerif:WorkPackage">

				<fo:block>

					<xsl:for-each select="cerif:WorkPackage/cerif:Index">	

					<fo:list-block border-width="{$width.border}"
							border-color="{$colour.border}"
							border-style="{$style.border}"
							margin-top="{$margin.top.main}"
							margin-left="{$margin.left.border}"
							margin-right="{$margin.right.border}"
							padding-before="{$padding.before.border}"
							padding-after="{$padding.after.border}"
							padding-start="{$padding.start.border}"
							padding-end="{$padding.end.border}"
							provisional-label-separation="10mm"
							provisional-distance-between-starts="50mm" end-indent="170mm" start-indent="70mm">

						<fo:list-item>

									<fo:list-item-label end-indent="label-end()">							
										
										<fo:block text-align="left" font-weight="bold">
												<xsl:call-template name="title">
													<xsl:with-param name="title" select="'Hauptverantwortlicher Projektpartner'"/>
													<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
												</xsl:call-template>	
												<xsl:value-of select="cerif:ResponsibleOrganisation"/>
										</fo:block>								
									</fo:list-item-label> 

									<fo:list-item-body start-indent="body-start()">
										<fo:block text-align="left">
														
												<xsl:call-template name="title">
													<xsl:with-param name="title" select="'Name Arbeitspaketes'"/>
													<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
												</xsl:call-template>
												<xsl:value-of select="cerif:Title"/>

												<xsl:call-template name="title">
													<xsl:with-param name="title" select="'Beschreibung des Arbeitspaketes'"/>
													<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
												</xsl:call-template>
												<xsl:value-of select="cerif:Description"/>

												<xsl:call-template name="title">
													<xsl:with-param name="title" select="'Abhängigkeiten'"/>
													<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
												</xsl:call-template>
												<xsl:value-of select="cerif:Requirement"/>
												
										</fo:block>
									</fo:list-item-body>

						</fo:list-item>

					</fo:list-block>

					</xsl:for-each>
			
				</fo:block>




	</xsl:if>

</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE WORK PACKAGE ENTITY  ALT -->	
<!--########################################################################-->

<xsl:template name="wp-alt">

<xsl:if test="cerif:WorkPackage">

	<xsl:for-each select="cerif:WorkPackage/cerif:Index">

		<fo:block border-width="{$width.border}"
							border-color="{$colour.border}"
							border-style="{$style.border}"
							margin-top="{$margin.top.main}"
							margin-left="{$margin.left.border}"
							margin-right="{$margin.right.border}"
							padding-before="{$padding.before.border}"
							padding-after="{$padding.after.border}"
							padding-start="{$padding.start.border}"
							padding-end="{$padding.end.border}">

							<fo:table table-layout="fixed">

							<fo:table-column column-width="100%"/> <!-- define the columns -->				
							
							<fo:table-header>
								<fo:table-cell column-number="1" border-width="thin"  border-style="solid">
									<fo:block text-align="left" font-weight="bold">
										<xsl:value-of select="cerif:Title"/>
									</fo:block>
								</fo:table-cell>
							</fo:table-header>
							
							<fo:table-body>					
									
									<fo:table-row>							

										<fo:table-cell column-number="1" border-width="thin" border-style="solid">
												<fo:block text-align="left">
												<xsl:call-template name="title">
													<xsl:with-param name="title" select="'Verantwortlicher Partner'"/>
													<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
												</xsl:call-template>
												<xsl:value-of select="cerif:Acronym"/>
												</fo:block>

										</fo:table-cell>
													 
									</fo:table-row>
														
									<fo:table-row>
							
										<fo:table-cell column-number="1" border-width="thin" border-style="solid">
											<fo:block text-align="left">
												<xsl:call-template name="title">
													<xsl:with-param name="title" select="'Beschreibung des Arbeitspaketes'"/>
													<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
												</xsl:call-template>
												<xsl:value-of select="cerif:Description"/>
											</fo:block>

										</fo:table-cell>
									 							
									</fo:table-row>

									<fo:table-row>

									<fo:table-cell column-number="1" border-width="thin" border-style="solid">
										<fo:block text-align="left">
											<xsl:call-template name="title">
												<xsl:with-param name="title" select="'Abhängigkeiten'"/>
												<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
											</xsl:call-template>
											<xsl:value-of select="cerif:Requirement"/>
										</fo:block>
									</fo:table-cell>

									</fo:table-row>

							</fo:table-body>				
							
							</fo:table>	

	  </fo:block> 		

	</xsl:for-each>

</xsl:if>

</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR Spinoff -->	
<!--########################################################################-->

<xsl:template name="spinoff">

	<xsl:if test="cerif:Spinoff">

		<xsl:for-each select="cerif:Spinoff/cerif:Index">

			<fo:block border-width="{$width.border}"
								border-color="{$colour.border}"
								border-style="{$style.border}"
								margin-top="{$margin.top.main}"
								margin-left="{$margin.left.border}"
								margin-right="{$margin.right.border}"
								padding-before="{$padding.before.border}"
								padding-after="{$padding.after.border}"
								padding-start="{$padding.start.border}"
								padding-end="{$padding.end.border}">

				<xsl:call-template name="value-single">
					<xsl:with-param name="value" select="cerif:Title"/>
					<xsl:with-param name="fontSize" select="$font.size.key-value"/>
					<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
				</xsl:call-template>	

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Beschreibung'"/>
					<xsl:with-param name="value" select="cerif:Description"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Ort des zuständigen Gerichts'"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:RegisterCourt"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Registerart'"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:RegisterType"/>
				</xsl:call-template>

								<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Registernummer'"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:RegisterNumber"/>
				</xsl:call-template>

								<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Datum der Eintragung'"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:FoundingDate"/>
				</xsl:call-template>

								<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Art der Ausgründung'"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:Type"/>
				</xsl:call-template>

								<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Wirtschaftszweig'"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:NACE"/>
				</xsl:call-template>

								<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Straße, Hausnummer'"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:Street"/>
				</xsl:call-template>

								<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Postleitzahl'"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:PostCode"/>
				</xsl:call-template>

								<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Ort'"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:City"/>
				</xsl:call-template>

								<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Land'"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:Country"/>
				</xsl:call-template>

								<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Homepage der Ausgründung'"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:WebAddress"/>
				</xsl:call-template>

					<xsl:call-template name="key-value-line-list" >				
							<xsl:with-param name = "key" select="'Ausgründende Organisation(en)'"/>
							<xsl:with-param name = "value" select="cerif:Result/cerif:RelatedOrganisation" />
							<xsl:with-param name="fontSize" select="$font.size.key-value"/>
						<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
					</xsl:call-template>

			<xsl:call-template name="title">
				<xsl:with-param name="title" select="'Unterstützungsleistung'"/>
				<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
			</xsl:call-template>


					<fo:table>
							<fo:table-column column-number="1" column-width="4cm"/>
							<fo:table-column column-number="2" column-width="4cm"/>
							<fo:table-column column-number="3" column-width="4cm"/>
							<fo:table-column column-number="4" column-width="4cm"/>
							<fo:table-column column-number="5" column-width="4cm"/>
							
							<fo:table-header>
								<fo:table-row>
								<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
									<fo:block text-align="left" 
								          font-size="{$font.size.standard}"
													font-weight="{$font.weight.key-value}"
													margin-top="{$margin.top.main}"
													padding-before="{$padding.before.border}"
													padding-after="{$padding.after.border}"
													padding-start="{$padding.start.border}"
													padding-end="{$padding.end.border}"
													margin-left="{$margin.left.border}"
													margin-right="{$margin.right.border}">
											<xsl:value-of select="'Jahr'"/>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
									<fo:block text-align="left" 
								          font-size="{$font.size.standard}"
													font-weight="{$font.weight.key-value}"
													margin-top="{$margin.top.main}"
													padding-before="{$padding.before.border}"
													padding-after="{$padding.after.border}"
													padding-start="{$padding.start.border}"
													padding-end="{$padding.end.border}"
													margin-left="{$margin.left.border}"
													margin-right="{$margin.right.border}">
											<xsl:value-of select="'Förderprogramm'"/>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
									<fo:block text-align="left" 
								          font-size="{$font.size.standard}"
													font-weight="{$font.weight.key-value}"
													margin-top="{$margin.top.main}"
													padding-before="{$padding.before.border}"
													padding-after="{$padding.after.border}"
													padding-start="{$padding.start.border}"
													padding-end="{$padding.end.border}"
													margin-left="{$margin.left.border}"
													margin-right="{$margin.right.border}">
											<xsl:value-of select="'Fördersumme'"/>
									</fo:block>
								</fo:table-cell>

									<fo:table-cell border-width="${width.border}"
													            border-color="{$colour.border}"
																border-style="{$style.border}">
									<fo:block text-align="left" 
								          font-size="{$font.size.standard}"
													font-weight="{$font.weight.key-value}"
													margin-top="{$margin.top.main}"
													padding-before="{$padding.before.border}"
													padding-after="{$padding.after.border}"
													padding-start="{$padding.start.border}"
													padding-end="{$padding.end.border}"
													margin-left="{$margin.left.border}"
													margin-right="{$margin.right.border}">
											<xsl:value-of select="'Währung'"/>
									</fo:block>
								</fo:table-cell>
							
									<fo:table-cell border-width="${width.border}"
													            border-color="{$colour.border}"
																border-style="{$style.border}">
									<fo:block text-align="left" 
								          font-size="{$font.size.standard}"
													font-weight="{$font.weight.key-value}"
													margin-top="{$margin.top.main}"
													padding-before="{$padding.before.border}"
													padding-after="{$padding.after.border}"
													padding-start="{$padding.start.border}"
													padding-end="{$padding.end.border}"
													margin-left="{$margin.left.border}"
													margin-right="{$margin.right.border}">
											<xsl:value-of select="'Nicht-finanzielle Unterstützung'"/>
									</fo:block>
								</fo:table-cell>
							
								</fo:table-row>
							</fo:table-header>

							<fo:table-body>
								<xsl:for-each select="cerif:Funding/cerif:Index">
								<fo:table-row>
									<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
										<fo:block text-align="left" 
															font-size="{$font.size.standard}"
															font-weight="{$font.weight.value}"
															margin-top="{$margin.top.main}"
															padding-before="{$padding.before.border}"
															padding-after="{$padding.after.border}"
															padding-start="{$padding.start.border}"
															padding-end="{$padding.end.border}"
															margin-left="{$margin.left.border}"
															margin-right="{$margin.right.border}">
											<xsl:value-of select="cerif:Year"/>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
										<fo:block text-align="left" 
															font-size="{$font.size.standard}"
															font-weight="{$font.weight.value}"
															margin-top="{$margin.top.main}"
															padding-before="{$padding.before.border}"
															padding-after="{$padding.after.border}"
															padding-start="{$padding.start.border}"
															padding-end="{$padding.end.border}"
															margin-left="{$margin.left.border}"
															margin-right="{$margin.right.border}">
											<xsl:value-of select="cerif:Title"/>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
										<fo:block text-align="left" 
															font-size="{$font.size.standard}"
															font-weight="{$font.weight.value}"
															margin-top="{$margin.top.main}"
															padding-before="{$padding.before.border}"
															padding-after="{$padding.after.border}"
															padding-start="{$padding.start.border}"
															padding-end="{$padding.end.border}"
															margin-left="{$margin.left.border}"
															margin-right="{$margin.right.border}">
											<xsl:value-of select="cerif:Amount"/>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
										<fo:block text-align="left" 
															font-size="{$font.size.standard}"
															font-weight="{$font.weight.value}"
															margin-top="{$margin.top.main}"
															padding-before="{$padding.before.border}"
															padding-after="{$padding.after.border}"
															padding-start="{$padding.start.border}"
															padding-end="{$padding.end.border}"
															margin-left="{$margin.left.border}"
															margin-right="{$margin.right.border}">
											<xsl:value-of select="cerif:Currency"/>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
										<fo:block text-align="left" 
															font-size="{$font.size.standard}"
															font-weight="{$font.weight.value}"
															margin-top="{$margin.top.main}"
															padding-before="{$padding.before.border}"
															padding-after="{$padding.after.border}"
															padding-start="{$padding.start.border}"
															padding-end="{$padding.end.border}"
															margin-left="{$margin.left.border}"
															margin-right="{$margin.right.border}">
											<xsl:value-of select="cerif:Description"/>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								</xsl:for-each>
							</fo:table-body>
							</fo:table>

			<xsl:call-template name="title">
				<xsl:with-param name="title" select="'Wirtschaftliche Entwicklung'"/>
				<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
			</xsl:call-template>


					<fo:table>
							<fo:table-column column-number="1" column-width="6cm"/>
							<fo:table-column column-number="2" column-width="6cm"/>
							<fo:table-column column-number="3" column-width="6cm"/>

							
							<fo:table-header>
								<fo:table-row>
								<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
									<fo:block text-align="left" 
								          font-size="{$font.size.standard}"
													font-weight="{$font.weight.key-value}"
													margin-top="{$margin.top.main}"
													padding-before="{$padding.before.border}"
													padding-after="{$padding.after.border}"
													padding-start="{$padding.start.border}"
													padding-end="{$padding.end.border}"
													margin-left="{$margin.left.border}"
													margin-right="{$margin.right.border}">
											<xsl:value-of select="'Jahr'"/>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
									<fo:block text-align="left" 
								          font-size="{$font.size.standard}"
													font-weight="{$font.weight.key-value}"
													margin-top="{$margin.top.main}"
													padding-before="{$padding.before.border}"
													padding-after="{$padding.after.border}"
													padding-start="{$padding.start.border}"
													padding-end="{$padding.end.border}"
													margin-left="{$margin.left.border}"
													margin-right="{$margin.right.border}">
											<xsl:value-of select="'Jahresumsatz'"/>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
									<fo:block text-align="left" 
								          font-size="{$font.size.standard}"
													font-weight="{$font.weight.key-value}"
													margin-top="{$margin.top.main}"
													padding-before="{$padding.before.border}"
													padding-after="{$padding.after.border}"
													padding-start="{$padding.start.border}"
													padding-end="{$padding.end.border}"
													margin-left="{$margin.left.border}"
													margin-right="{$margin.right.border}">
											<xsl:value-of select="'Währung'"/>
									</fo:block>
								</fo:table-cell>		
								</fo:table-row>
							</fo:table-header>

							<fo:table-body>
								<xsl:for-each select="cerif:Performance/cerif:Index">
								<fo:table-row>
									<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
										<fo:block text-align="left" 
															font-size="{$font.size.standard}"
															font-weight="{$font.weight.value}"
															margin-top="{$margin.top.main}"
															padding-before="{$padding.before.border}"
															padding-after="{$padding.after.border}"
															padding-start="{$padding.start.border}"
															padding-end="{$padding.end.border}"
															margin-left="{$margin.left.border}"
															margin-right="{$margin.right.border}">
											<xsl:value-of select="cerif:Year"/>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
										<fo:block text-align="left" 
															font-size="{$font.size.standard}"
															font-weight="{$font.weight.value}"
															margin-top="{$margin.top.main}"
															padding-before="{$padding.before.border}"
															padding-after="{$padding.after.border}"
															padding-start="{$padding.start.border}"
															padding-end="{$padding.end.border}"
															margin-left="{$margin.left.border}"
															margin-right="{$margin.right.border}">
											<xsl:value-of select="cerif:Amount"/>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
										<fo:block text-align="left" 
															font-size="{$font.size.standard}"
															font-weight="{$font.weight.value}"
															margin-top="{$margin.top.main}"
															padding-before="{$padding.before.border}"
															padding-after="{$padding.after.border}"
															padding-start="{$padding.start.border}"
															padding-end="{$padding.end.border}"
															margin-left="{$margin.left.border}"
															margin-right="{$margin.right.border}">
											<xsl:value-of select="cerif:Currency"/>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								</xsl:for-each>
							</fo:table-body>
							</fo:table>






			</fo:block> 		

		</xsl:for-each>

	</xsl:if>

</xsl:template>


<!--########################################################################-->
<!-- TEMPLATE FOR THE AWARD ENTITY -->	
<!--########################################################################-->

<xsl:template name="award">

	<xsl:if test="cerif:Award">

		<xsl:for-each select="cerif:Award/cerif:Index">

			<fo:block border-width="{$width.border}"
								border-color="{$colour.border}"
								border-style="{$style.border}"
								margin-top="{$margin.top.main}"
								margin-left="{$margin.left.border}"
								margin-right="{$margin.right.border}"
								padding-before="{$padding.before.border}"
								padding-after="{$padding.after.border}"
								padding-start="{$padding.start.border}"
								padding-end="{$padding.end.border}">

				<xsl:call-template name="value-single">
					<xsl:with-param name="value" select="cerif:Name"/>
					<xsl:with-param name="fontSize" select="$font.size.key-value"/>
					<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
				</xsl:call-template>	

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Beschreibung'"/>
					<xsl:with-param name="value" select="cerif:Description"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Datum der Verleihung'"/>
					<xsl:with-param name="value" select="cerif:Date"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Preisgeld'"/>
					<xsl:with-param name="value" select="cerif:PrizeMoney"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Währung'"/>
					<xsl:with-param name="value" select="cerif:Currency"/>
				</xsl:call-template>

					<fo:table>
							<fo:table-column column-number="1" column-width="9cm"/>
							<fo:table-column column-number="2" column-width="9cm"/>
								<fo:table-body>
								<fo:table-row>
									<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
										<fo:block text-align="left" 
															font-size="{$font.size.standard}"
															font-weight="{$font.weight.value}"
															margin-top="{$margin.top.main}"
															padding-before="{$padding.before.border}"
															padding-after="{$padding.after.border}"
															padding-start="{$padding.start.border}"
															padding-end="{$padding.end.border}"
															margin-left="{$margin.left.border}"
															margin-right="{$margin.right.border}">
													<xsl:call-template name="key-value-line-list" >
													<xsl:with-param name="key" select= "'Ausgezeichnete Person(en)'"/>
														<xsl:with-param name = "value" select="cerif:Winner"/>
														<xsl:with-param name="fontSize" select="$font.size.key-value"/>
													<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
													</xsl:call-template>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
										<fo:block text-align="left" 
															font-size="{$font.size.standard}"
															font-weight="{$font.weight.value}"
															margin-top="{$margin.top.main}"
															padding-before="{$padding.before.border}"
															padding-after="{$padding.after.border}"
															padding-start="{$padding.start.border}"
															padding-end="{$padding.end.border}"
															margin-left="{$margin.left.border}"
															margin-right="{$margin.right.border}">
													<xsl:call-template name="key-value-line-list" >
														<xsl:with-param name="key" select= "'Ausgezeichnete Organisation(en)'"/>
														<xsl:with-param name = "value" select="cerif:Organisation"/>
														<xsl:with-param name="fontSize" select="$font.size.key-value"/>
													<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
													</xsl:call-template>
											</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
							</fo:table>

				<xsl:call-template name="key-value-line-list" >
					<xsl:with-param name = "key" select="'Initiator(en)'"/>
					<xsl:with-param name = "value" select="cerif:Donor"/>
					<xsl:with-param name="fontSize" select="$font.size.key-value"/>
				<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Akteursgruppe des Initiators'"/>
					<xsl:with-param name="value" select="cerif:TargetGroup"/>
				</xsl:call-template>

			</fo:block> 		

		</xsl:for-each>

	</xsl:if>

</xsl:template>


<!--########################################################################-->
<!-- TEMPLATE FOR THE OPEN RESEARCH QUESTIONS ENTITY -->	
<!--########################################################################-->

<xsl:template name="open-research-question">

	<xsl:if test="cerif:OpenResearchQuestion">

		<xsl:for-each select="cerif:OpenResearchQuestion/cerif:Index">

			<fo:block border-width="{$width.border}"
								border-color="{$colour.border}"
								border-style="{$style.border}"
								margin-top="{$margin.top.main}"
								margin-left="{$margin.left.border}"
								margin-right="{$margin.right.border}"
								padding-before="{$padding.before.border}"
								padding-after="{$padding.after.border}"
								padding-start="{$padding.start.border}"
								padding-end="{$padding.end.border}">

				<xsl:call-template name="value-single">
					<xsl:with-param name="value" select="cerif:Title"/>
					<xsl:with-param name="fontSize" select="$font.size.key-value"/>
					<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
				</xsl:call-template>	

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Beschreibung?'"/>
					<xsl:with-param name="value" select="cerif:Description"/>
				</xsl:call-template>

			</fo:block> 		

		</xsl:for-each>

	</xsl:if>

</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE BOUNDARY CONDITIONS -->	
<!--########################################################################-->

<xsl:template name="condition">

	<xsl:if test="cerif:Condition">

		<xsl:for-each select="cerif:Condition/cerif:Index">

			<fo:block border-width="{$width.border}"
								border-color="{$colour.border}"
								border-style="{$style.border}"
								margin-top="{$margin.top.main}"
								margin-left="{$margin.left.border}"
								margin-right="{$margin.right.border}"
								padding-before="{$padding.before.border}"
								padding-after="{$padding.after.border}"
								padding-start="{$padding.start.border}"
								padding-end="{$padding.end.border}">

				<xsl:call-template name="value-single">
					<xsl:with-param name="value" select="cerif:Title"/>
					<xsl:with-param name="fontSize" select="$font.size.key-value"/>
					<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
				</xsl:call-template>	

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Art der Rahmenbedingung'"/>
					<xsl:with-param name="value" select="cerif:ConditionType"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Einfluss auf die Wirkung des Projektes'"/>
					<xsl:with-param name="value" select="cerif:Influence"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Startzeitpunkt der Wirksamkeit'"/>
					<xsl:with-param name="value" select="cerif:StartFramework"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Beschreibung'"/>
					<xsl:with-param name="value" select="cerif:ConditionDescription"/>
				</xsl:call-template>

			</fo:block> 		

		</xsl:for-each>

	</xsl:if>

</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE EXPECTED SOCIETAL IMPACT ENTITY -->	
<!--########################################################################-->

<xsl:template name="expected-societal-impact">

<xsl:if test="cerif:ExpectedSocietalImpact">

	<xsl:for-each select="cerif:ExpectedSocietalImpact/cerif:Index">

		<fo:block border-width="{$width.border}"
							border-color="{$colour.border}"
							border-style="{$style.border}"
							margin-top="{$margin.top.main}"
							margin-left="{$margin.left.border}"
							margin-right="{$margin.right.border}"
							padding-before="{$padding.before.border}"
							padding-after="{$padding.after.border}"
							padding-start="{$padding.start.border}"
							padding-end="{$padding.end.border}">

				<xsl:call-template name="value-single">
					<xsl:with-param name="value" select="cerif:Title"/>
					<xsl:with-param name="fontSize" select="$font.size.key-value"/>
					<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
				</xsl:call-template>	

							<xsl:call-template name="key-value-single">
				<xsl:with-param name="key" select="'Beschreibung'"/>
				<xsl:with-param name="value" select="cerif:Description"/>
			</xsl:call-template>

				<xsl:call-template name="key-value-single">
				<xsl:with-param name="key" select="'Art der Rahmenbedingung'"/>
				<xsl:with-param name="value" select="cerif:TransitionArea"/>
			</xsl:call-template>

			<xsl:call-template name="key-value-single">
				<xsl:with-param name="key" select="'Einfluss auf die Wirkung des Projektes'"/>
				<xsl:with-param name="value" select="cerif:Evidence"/>
			</xsl:call-template>

			<xsl:call-template name="title">
				<xsl:with-param name="title" select="'Art des Einflusses'"/>
				<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
			</xsl:call-template>

					<fo:table>
							<fo:table-column column-number="1" column-width="6cm"/>
							<fo:table-column column-number="2" column-width="6cm"/>
							<fo:table-column column-number="3" column-width="8cm"/>
							
							<fo:table-header>
								<fo:table-row>
								<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
									<fo:block text-align="left" 
								          font-size="{$font.size.standard}"
													font-weight="{$font.weight.key-value}"
													margin-top="{$margin.top.main}"
													padding-before="{$padding.before.border}"
													padding-after="{$padding.after.border}"
													padding-start="{$padding.start.border}"
													padding-end="{$padding.end.border}"
													margin-left="{$margin.left.border}"
													margin-right="{$margin.right.border}">
											<xsl:value-of select="'Hauptelement'"/>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
									<fo:block text-align="left" 
								          font-size="{$font.size.standard}"
													font-weight="{$font.weight.key-value}"
													margin-top="{$margin.top.main}"
													padding-before="{$padding.before.border}"
													padding-after="{$padding.after.border}"
													padding-start="{$padding.start.border}"
													padding-end="{$padding.end.border}"
													margin-left="{$margin.left.border}"
													margin-right="{$margin.right.border}">
											<xsl:value-of select="'Wirkungsintensität'"/>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
									<fo:block text-align="left" 
								          font-size="{$font.size.standard}"
													font-weight="{$font.weight.key-value}"
													margin-top="{$margin.top.main}"
													padding-before="{$padding.before.border}"
													padding-after="{$padding.after.border}"
													padding-start="{$padding.start.border}"
													padding-end="{$padding.end.border}"
													margin-left="{$margin.left.border}"
													margin-right="{$margin.right.border}">
											<xsl:value-of select="'Umfang der erwarteten Wirkung'"/>
									</fo:block>
								</fo:table-cell>
								</fo:table-row>
							</fo:table-header>

							<fo:table-body>
								<xsl:for-each select="cerif:Type/cerif:Index">
								<fo:table-row>
									<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
										<fo:block text-align="left" 
															font-size="{$font.size.standard}"
															font-weight="{$font.weight.value}"
															margin-top="{$margin.top.main}"
															padding-before="{$padding.before.border}"
															padding-after="{$padding.after.border}"
															padding-start="{$padding.start.border}"
															padding-end="{$padding.end.border}"
															margin-left="{$margin.left.border}"
															margin-right="{$margin.right.border}">
											<xsl:value-of select="cerif:CoreElement"/>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
										<fo:block text-align="left" 
															font-size="{$font.size.standard}"
															font-weight="{$font.weight.value}"
															margin-top="{$margin.top.main}"
															padding-before="{$padding.before.border}"
															padding-after="{$padding.after.border}"
															padding-start="{$padding.start.border}"
															padding-end="{$padding.end.border}"
															margin-left="{$margin.left.border}"
															margin-right="{$margin.right.border}">
											<xsl:value-of select="cerif:Intensity"/>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
										<fo:block text-align="left" 
															font-size="{$font.size.standard}"
															font-weight="{$font.weight.value}"
															margin-top="{$margin.top.main}"
															padding-before="{$padding.before.border}"
															padding-after="{$padding.after.border}"
															padding-start="{$padding.start.border}"
															padding-end="{$padding.end.border}"
															margin-left="{$margin.left.border}"
															margin-right="{$margin.right.border}">
											<xsl:value-of select="cerif:Description"/>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								</xsl:for-each>
							</fo:table-body>
							</fo:table>
			
	  </fo:block> 		

	</xsl:for-each>

</xsl:if>

</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE NEGATIVE SIDE EFFECTS ENTITY -->	
<!--########################################################################-->

<xsl:template name="negative-side-effect">

<xsl:if test="cerif:NegativeSideEffect">

	<xsl:for-each select="cerif:NegativeSideEffect/cerif:Index">

		<fo:block border-width="{$width.border}"
							border-color="{$colour.border}"
							border-style="{$style.border}"
							margin-top="{$margin.top.main}"
							margin-left="{$margin.left.border}"
							margin-right="{$margin.right.border}"
							padding-before="{$padding.before.border}"
							padding-after="{$padding.after.border}"
							padding-start="{$padding.start.border}"
							padding-end="{$padding.end.border}">

				<xsl:call-template name="value-single">
					<xsl:with-param name="value" select="cerif:Title"/>
					<xsl:with-param name="fontSize" select="$font.size.key-value"/>
					<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
				</xsl:call-template>	

							<xsl:call-template name="key-value-single">
				<xsl:with-param name="key" select="'Beschreibung'"/>
				<xsl:with-param name="value" select="cerif:Description"/>
			</xsl:call-template>

				<xsl:call-template name="key-value-single">
				<xsl:with-param name="key" select="'Adressierter Transformationsbereich'"/>
				<xsl:with-param name="value" select="cerif:TransitionArea"/>
			</xsl:call-template>

			<xsl:call-template name="key-value-single">
				<xsl:with-param name="key" select="'Fachliche Grundlage'"/>
				<xsl:with-param name="value" select="cerif:Evidence"/>
			</xsl:call-template>

			<xsl:call-template name="title">
				<xsl:with-param name="title" select="'Art des Einflusses'"/>
				<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
			</xsl:call-template>

					<fo:table>
							<fo:table-column column-number="1" column-width="7cm"/>
							<fo:table-column column-number="2" column-width="5cm"/>
							<fo:table-column column-number="3" column-width="8cm"/>
							
							<fo:table-header>
								<fo:table-row>
								<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
									<fo:block text-align="left" 
								          font-size="{$font.size.standard}"
													font-weight="{$font.weight.key-value}"
													margin-top="{$margin.top.main}"
													padding-before="{$padding.before.border}"
													padding-after="{$padding.after.border}"
													padding-start="{$padding.start.border}"
													padding-end="{$padding.end.border}"
													margin-left="{$margin.left.border}"
													margin-right="{$margin.right.border}">
											<xsl:value-of select="'Kernelement der Nachhaltigkeit'" />
									</fo:block>
								</fo:table-cell>

								<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
									<fo:block text-align="left" 
								          font-size="{$font.size.standard}"
													font-weight="{$font.weight.key-value}"
													margin-top="{$margin.top.main}"
													padding-before="{$padding.before.border}"
													padding-after="{$padding.after.border}"
													padding-start="{$padding.start.border}"
													padding-end="{$padding.end.border}"
													margin-left="{$margin.left.border}"
													margin-right="{$margin.right.border}">
											<xsl:value-of select="'Intensität der negativen Nebenwirkung'" />
									</fo:block>
								</fo:table-cell>

								<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
									<fo:block text-align="left" 
								          font-size="{$font.size.standard}"
													font-weight="{$font.weight.key-value}"
													margin-top="{$margin.top.main}"
													padding-before="{$padding.before.border}"
													padding-after="{$padding.after.border}"
													padding-start="{$padding.start.border}"
													padding-end="{$padding.end.border}"
													margin-left="{$margin.left.border}"
													margin-right="{$margin.right.border}">
											<xsl:value-of select="'Umfang der negativen Wirkung'"/>
									</fo:block>
								</fo:table-cell>
								</fo:table-row>
							</fo:table-header>

							<fo:table-body>
								<xsl:for-each select="cerif:Type/cerif:Index">
								<fo:table-row>
									<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
										<fo:block text-align="left" 
															font-size="{$font.size.standard}"
															font-weight="{$font.weight.value}"
															margin-top="{$margin.top.main}"
															padding-before="{$padding.before.border}"
															padding-after="{$padding.after.border}"
															padding-start="{$padding.start.border}"
															padding-end="{$padding.end.border}"
															margin-left="{$margin.left.border}"
															margin-right="{$margin.right.border}">
											<xsl:value-of select="cerif:CoreElement"/>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
										<fo:block text-align="left" 
															font-size="{$font.size.standard}"
															font-weight="{$font.weight.value}"
															margin-top="{$margin.top.main}"
															padding-before="{$padding.before.border}"
															padding-after="{$padding.after.border}"
															padding-start="{$padding.start.border}"
															padding-end="{$padding.end.border}"
															margin-left="{$margin.left.border}"
															margin-right="{$margin.right.border}">
											<xsl:value-of select="cerif:Intensity"/>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
										<fo:block text-align="left" 
															font-size="{$font.size.standard}"
															font-weight="{$font.weight.value}"
															margin-top="{$margin.top.main}"
															padding-before="{$padding.before.border}"
															padding-after="{$padding.after.border}"
															padding-start="{$padding.start.border}"
															padding-end="{$padding.end.border}"
															margin-left="{$margin.left.border}"
															margin-right="{$margin.right.border}">
											<xsl:value-of select="cerif:Description"/>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								</xsl:for-each>
							</fo:table-body>
							</fo:table>
			



	  </fo:block> 		

	</xsl:for-each>

</xsl:if>

</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE ETHICS ENTITY -->	
<!--########################################################################-->

<xsl:template name="ethics">

<xsl:if test="cerif:Ethic">

	<xsl:for-each select="cerif:Ethic/cerif:Index">

		<fo:block border-width="{$width.border}"
							border-color="{$colour.border}"
							border-style="{$style.border}"
							margin-top="{$margin.top.main}"
							margin-left="{$margin.left.border}"
							margin-right="{$margin.right.border}"
							padding-before="{$padding.before.border}"
							padding-after="{$padding.after.border}"
							padding-start="{$padding.start.border}"
							padding-end="{$padding.end.border}">

				<xsl:call-template name="value-single">
					<xsl:with-param name="value" select="cerif:KeyMessage"/>
					<xsl:with-param name="fontSize" select="$font.size.key-value"/>
					<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
				</xsl:call-template>

			<xsl:call-template name="title">
				<xsl:with-param name="title" select="'Ethische Reflexion'"/>
				<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
			</xsl:call-template>

					<fo:table>
							<fo:table-column column-number="1" column-width="8cm"/>
							<fo:table-column column-number="2" column-width="12cm"/>
													
							<fo:table-header>
								<fo:table-row>
								<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
									<fo:block text-align="left" 
								          font-size="{$font.size.standard}"
													font-weight="{$font.weight.key-value}"
													margin-top="{$margin.top.main}"
													padding-before="{$padding.before.border}"
													padding-after="{$padding.after.border}"
													padding-start="{$padding.start.border}"
													padding-end="{$padding.end.border}"
													margin-left="{$margin.left.border}"
													margin-right="{$margin.right.border}">
											<xsl:value-of select="'Relevanter ethischer Aspekt'" />
									</fo:block>
								</fo:table-cell>

								<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
									<fo:block text-align="left" 
								          font-size="{$font.size.standard}"
													font-weight="{$font.weight.key-value}"
													margin-top="{$margin.top.main}"
													padding-before="{$padding.before.border}"
													padding-after="{$padding.after.border}"
													padding-start="{$padding.start.border}"
													padding-end="{$padding.end.border}"
													margin-left="{$margin.left.border}"
													margin-right="{$margin.right.border}">
											<xsl:value-of select="'Vorgehen im Vorhaben für einen angemessenen Umgang'" />
									</fo:block>
								</fo:table-cell>
								</fo:table-row>
							</fo:table-header>

							<fo:table-body>
								<xsl:for-each select="cerif:Reflection/cerif:Index">
								<fo:table-row>
									<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
										<fo:block text-align="left" 
															font-size="{$font.size.standard}"
															font-weight="{$font.weight.value}"
															margin-top="{$margin.top.main}"
															padding-before="{$padding.before.border}"
															padding-after="{$padding.after.border}"
															padding-start="{$padding.start.border}"
															padding-end="{$padding.end.border}"
															margin-left="{$margin.left.border}"
															margin-right="{$margin.right.border}">
											<xsl:value-of select="cerif:AspectType"/>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
										<fo:block text-align="left" 
															font-size="{$font.size.standard}"
															font-weight="{$font.weight.value}"
															margin-top="{$margin.top.main}"
															padding-before="{$padding.before.border}"
															padding-after="{$padding.after.border}"
															padding-start="{$padding.start.border}"
															padding-end="{$padding.end.border}"
															margin-left="{$margin.left.border}"
															margin-right="{$margin.right.border}">
											<xsl:value-of select="cerif:EthicDescription"/>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								</xsl:for-each>
							</fo:table-body>
							</fo:table>		

				<xsl:call-template name="key-value-single">
				<xsl:with-param name="key" select="'Weitergehende Dokumente verfügbar'"/>
				<xsl:with-param name="value" select="cerif:FurtherDocuments"/>
			</xsl:call-template>

	  </fo:block> 		

	</xsl:for-each>

</xsl:if>

</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE GENDER ENTITY -->	
<!--########################################################################-->

<xsl:template name="gender">

	<xsl:if test="cerif:Gender">

		<xsl:for-each select="cerif:Gender/cerif:Index">

			<fo:block border-width="{$width.border}"
								border-color="{$colour.border}"
								border-style="{$style.border}"
								margin-top="{$margin.top.main}"
								margin-left="{$margin.left.border}"
								margin-right="{$margin.right.border}"
								padding-before="{$padding.before.border}"
								padding-after="{$padding.after.border}"
								padding-start="{$padding.start.border}"
								padding-end="{$padding.end.border}">

				<xsl:call-template name="value-single">
					<xsl:with-param name="value" select="cerif:KeyMessage"/>
					<xsl:with-param name="fontSize" select="$font.size.key-value"/>
					<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
				</xsl:call-template>	

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Gibt es einen Geschlechterbezug in der Problemstellung?'"/>
					<xsl:with-param name="value" select="cerif:Reflection/cerif:Researchquestion"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Erläuterung zur Problemstellung'"/>
					<xsl:with-param name="value" select="cerif:Reflection/cerif:DescriptionResearchquestion"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Gibt es einen Geschlechterbezug im Forschungsstand?'"/>
					<xsl:with-param name="value" select="cerif:Reflection/cerif:StateOfResearch"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Erläuterung zum Foschungsstand'"/>
					<xsl:with-param name="value" select="cerif:Reflection/cerif:DescriptionStateOfResearch"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Berücksichtigung von Geschlechteraspekten im Projektdesign'"/>
					<xsl:with-param name="value" select="cerif:Reflection/cerif:Relevance"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Erläuterung zu Geschlechteraspekten im Projektdesign'"/>
					<xsl:with-param name="value" select="cerif:Reflection/cerif:DescriptionRelevance"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Berücksichtigung geschlechterspezifischer Unterschiede hinsichtlich der Anwendung und Wirkung der Projektergebnisse'"/>
					<xsl:with-param name="value" select="cerif:Reflection/cerif:Application"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Erläuterung zu geschlechtsspezifischen Unterschiede hinsichtlich der Anwendung und Wirkung'"/>
					<xsl:with-param name="value" select="cerif:Reflection/cerif:DescriptionApplication"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Umsetzung direkter Maßnahmen zur Förderung der Gleichstellung bei den Beschäftigten im Projekt'"/>
					<xsl:with-param name="value" select="cerif:Reflection/cerif:Implementation"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Erläuterung zu Maßnahmen zur Förderung der Gleichstellung im Projekt'"/>
					<xsl:with-param name="value" select="cerif:Reflection/cerif:DescriptionImplementaiton"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Geschlechterverhältnis bei den Beschäftigten im Projekt'"/>
					<xsl:with-param name="value" select="cerif:Reflection/cerif:GenderBalance"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="'Erläuterung zum Geschlechterverhältnis'"/>
					<xsl:with-param name="value" select="cerif:Reflection/cerif:DescriptionGenderBalance"/>
				</xsl:call-template>

			</fo:block> 		

		</xsl:for-each>

	</xsl:if>

</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE COOPERATION PARTNER ENTITY -->	
<!--########################################################################-->

	<xsl:template name="cooperation-partner">

		<xsl:if test="cerif:CooperationPartner">

			<xsl:for-each select="cerif:CooperationPartner/cerif:Index">

				<fo:block border-width="{$width.border}"
									border-color="{$colour.border}"
									border-style="{$style.border}"
									margin-top="{$margin.top.main}"
									margin-left="{$margin.left.border}"
									margin-right="{$margin.right.border}"
									padding-before="{$padding.before.border}"
									padding-after="{$padding.after.border}"
									padding-start="{$padding.start.border}"
									padding-end="{$padding.end.border}">
									
					<xsl:call-template name="value-single">
						<xsl:with-param name="value" select="cerif:OrganisationUnit/cerif:OrganisationName"/>
						<xsl:with-param name="fontSize" select="$font.size.key-value"/>
						<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
					</xsl:call-template>

					<xsl:call-template name="key-value-single"> 
						<xsl:with-param name="key" select="$lang.organisation.form"/>
						<xsl:with-param name="value" select="cerif:OrganisationUnit/cerif:OrganisationType"/>
				  </xsl:call-template>	

					<xsl:call-template name="key-value-single"> 
						<xsl:with-param name="key" select="$lang.cooperation.type"/>
						<xsl:with-param name="value" select="cerif:CooperationType"/>
				  </xsl:call-template>	

					<xsl:call-template name="key-value-single"> 
						<xsl:with-param name="key" select="$lang.organisation.field-of-activity"/>
						<xsl:with-param name="value" select="cerif:FieldOfActivity"/>
				  </xsl:call-template>	

					<xsl:call-template name="key-value-single"> 
						<xsl:with-param name="key" select="$lang.organisation.nace"/>
						<xsl:with-param name="value" select="cerif:NACE"/>
				  </xsl:call-template>	

					<xsl:call-template name="key-value-single"> 
						<xsl:with-param name="key" select="$lang.organisation.destatis"/>
						<xsl:with-param name="value" select="cerif:Destatis"/>
				  </xsl:call-template>	

					<xsl:call-template name="key-value-comma-list"> 
						<xsl:with-param name="key" select="$lang.organisation.political-level"/>
						<xsl:with-param name="value" select="cerif:PoliticalLevel"/>
				  </xsl:call-template>	

					<xsl:call-template name="key-value-single"> 
						<xsl:with-param name="key" select="$lang.organisation.project-contribution"/>
						<xsl:with-param name="value" select="cerif:ProjectContribution"/>
				  </xsl:call-template>	

					<xsl:call-template name="key-postcode-city-country">
						<xsl:with-param name="key" select="$lang.postcode-city-country"/>
						<xsl:with-param name="postcode" select="cerif:OrganisationUnit/cerif:PostCode"/>
						<xsl:with-param name="city" select="cerif:OrganisationUnit/cerif:City"/>
						<xsl:with-param name="country" select="cerif:OrganisationUnit/cerif:Country"/>
				  </xsl:call-template>

					<xsl:call-template name="key-value-comma-list"> 
						<xsl:with-param name="key" select="$lang.website"/>
						<xsl:with-param name="value" select="cerif:OrganisationUnit/cerif:WebAddress"/>
				  </xsl:call-template>					

				</fo:block> 		

			</xsl:for-each> 

	  </xsl:if>

	</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE TARGET GROUP ENTITY -->	
<!--########################################################################-->

	<xsl:template name="target-group">

		<xsl:if test="cerif:TargetGroup">

			<xsl:for-each select="cerif:TargetGroup/cerif:Index">

				<fo:block border-width="{$width.border}"
									border-color="{$colour.border}"
									border-style="{$style.border}"
									margin-top="{$margin.top.main}"
									margin-left="{$margin.left.border}"
									margin-right="{$margin.right.border}"
									padding-before="{$padding.before.border}"
									padding-after="{$padding.after.border}"
									padding-start="{$padding.start.border}"
									padding-end="{$padding.end.border}">
									
					<xsl:call-template name="value-single">
						<xsl:with-param name="value" select="cerif:Title"/>
						<xsl:with-param name="fontSize" select="$font.size.key-value"/>
						<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
					</xsl:call-template>

					<xsl:call-template name="key-value-single"> 
						<xsl:with-param name="key" select="$lang.targetgroup.relevance"/>
						<xsl:with-param name="value" select="cerif:ProjectRelevance"/>
				  </xsl:call-template>	

					<xsl:call-template name="key-value-single"> 
						<xsl:with-param name="key" select="$lang.organisation.field-of-activity"/>
						<xsl:with-param name="value" select="cerif:Classification/cerif:FieldOfActivity"/>
				  </xsl:call-template>	

					<xsl:call-template name="key-value-single"> 
						<xsl:with-param name="key" select="$lang.organisation.nace"/>
						<xsl:with-param name="value" select="cerif:Classification/cerif:NACE"/>
				  </xsl:call-template>	

					<xsl:call-template name="key-value-comma-list"> 
						<xsl:with-param name="key" select="$lang.organisation.political-level"/>
						<xsl:with-param name="value" select="cerif:Classification/cerif:PoliticalLevel"/>
				  </xsl:call-template>	

					<xsl:call-template name="key-value-single"> 
						<xsl:with-param name="key" select="$lang.organisation.size"/>
						<xsl:with-param name="value" select="cerif:Classification/cerif:BusinessSize"/>
				  </xsl:call-template>	

					<xsl:call-template name="key-value-single"> 
						<xsl:with-param name="key" select="$lang.organisation.location-type"/>
						<xsl:with-param name="value" select="cerif:Classification/cerif:LocationType"/>
				  </xsl:call-template>	

				</fo:block> 		

			</xsl:for-each> 

	  </xsl:if>

	</xsl:template>

<!--########################################################################-->
<!-- GENERAL TEMPLATES -->	
<!--########################################################################-->

<!--==============================================================-->
<!-- vertical gap -->	
<!--==============================================================-->

	<xsl:template name="vertical-gap" >
		<xsl:param name = "margin"/>

		<fo:block margin-left="{$margin.left.text}"
							margin-top="{$margin}">
		</fo:block>

	</xsl:template>

<!--==============================================================-->	
<!-- title -->	
<!--==============================================================-->

	<xsl:template name="title" >
		<xsl:param name = "title"/>
		<xsl:param name = "fontSize"/>

		<fo:block margin-left="{$margin.left.text}"
							margin-top="{$margin.top.title}">
			<fo:inline font-size="{$fontSize}"
								 font-weight="{$font.weight.title}"
								 text-align="{$text.alignment.title}">							
				<xsl:value-of select="$title"/>
			</fo:inline>
		</fo:block>

	</xsl:template>

<!--==============================================================-->
<!-- section title with counter -->	
<!--==============================================================-->

	<xsl:template name="title-section-counter" >
		<xsl:param name = "section"/>
		<xsl:param name = "counter"/>
		<xsl:param name = "title"/>
		<xsl:param name = "fontSize"/>
		<xsl:param name = "length.ruler"/>

		<fo:block margin-left="{$margin.left.text}"
							margin-top="{$margin.top.title}">

			<xsl:if test="$section">							
				<fo:inline font-size="{$fontSize}"
								   font-weight="{$font.weight.title}"
								   text-align="{$text.alignment.title}">							
					<xsl:value-of select="$section"/>
				</fo:inline>
      </xsl:if>

			<xsl:if test="$counter">			
				<fo:inline font-size="{$fontSize}"
								   font-weight="{$font.weight.title}"
								   text-align="{$text.alignment.title}">							
					<xsl:value-of select="$counter"/>
				</fo:inline>
      </xsl:if>

			<fo:inline font-size="{$fontSize}"
								 font-weight="{$font.weight.title}"
								 text-align="{$text.alignment.title}">							
				<xsl:value-of select="$title"/>
			</fo:inline>

			<xsl:if test="$length.ruler">   
				<fo:block>
					<fo:leader leader-pattern="rule" 
										 leader-length="{$length.ruler}"
										 rule-style="solid" />         
				</fo:block>
      </xsl:if>

		</fo:block>

	</xsl:template>

<!--==============================================================-->	
<!--  single value -->
<!--==============================================================-->

	<xsl:template name="value-single" >
	  <xsl:param name = "value"/>
		<xsl:param name = "fontSize"/>
		<xsl:param name = "fontWeight"/>

	  <xsl:if test="$value">
		  <fo:block margin-left="{$margin.left.text}"
								font-size="{$fontSize}" 
			        	margin-top="{$margin.top.main}">

				<xsl:choose> 
					<xsl:when test="$fontWeight">								
						<fo:inline font-weight="{$fontWeight}" 
											 text-align="{$text.alignment.value}">
							<xsl:value-of select="$value"/> 
						</fo:inline>
					</xsl:when>
					<xsl:otherwise>
						<fo:inline font-weight="{$font.weight.value}" 
											 text-align="{$text.alignment.value}">
							<xsl:value-of select="$value"/> 
						</fo:inline>
					</xsl:otherwise>
			  </xsl:choose>

			</fo:block>
	  </xsl:if>
	</xsl:template>

<!--==============================================================-->	
<!-- key: no value -->
<!--==============================================================-->

	<xsl:template name="key-only" >
	  <xsl:param name = "key"/>

	  <xsl:if test="$key">
		  <fo:block margin-left="{$margin.left.text}"
								font-size="{$font.size.standard}" 
			        	margin-top="{$margin.top.main}">

				<fo:inline font-weight="{$font.weight.key}" 
									 text-align="{$text.alignment.key}">
					<xsl:value-of select="$key"/> 
				</fo:inline>

				<xsl:text>: </xsl:text>

			</fo:block>
	  </xsl:if>
	</xsl:template>

<!--==============================================================-->	
<!-- key: single value -->
<!--==============================================================-->

	<xsl:template name="key-value-single" >
	  <xsl:param name = "key"/>
	  <xsl:param name = "value"/>

	  <xsl:if test="$value">
		  <fo:block margin-left="{$margin.left.text}"
								font-size="{$font.size.standard}" 
			        	margin-top="{$margin.top.main}">

				<fo:inline font-weight="{$font.weight.key}" 
				           text-align="{$text.alignment.key}">
					<xsl:value-of select="$key"/> 
				</fo:inline>

				<xsl:text>: </xsl:text>

				<fo:inline font-weight="{$font.weight.value}" 
									 text-align="{$text.alignment.value}">
					<xsl:value-of select="$value"/> 
				</fo:inline>

			</fo:block>
	  </xsl:if>
	</xsl:template>
	
<!--==============================================================-->	
<!-- key: period -->
<!--==============================================================-->

	<xsl:template name="key-period" >
	  <xsl:param name = "key"/>
	  <xsl:param name = "startdate"/>
		<xsl:param name = "enddate"/>

	  <xsl:if test="$startdate">
			<xsl:if test="$enddate">
				<fo:block margin-left="{$margin.left.text}"
									font-size="{$font.size.standard}" 
									margin-top="{$margin.top.main}">

					<fo:inline font-weight="{$font.weight.key}" 
										 text-align="{$text.alignment.key}">
						<xsl:value-of select="$key"/> 
					</fo:inline>

					<xsl:text>: </xsl:text>

					<fo:inline font-weight="{$font.weight.value}" 
										 text-align="{$text.alignment.value}">
						<xsl:value-of select="$startdate"/> 
					</fo:inline>

					<xsl:text> - </xsl:text>

					<fo:inline font-weight="{$font.weight.value}" 
										 text-align="{$text.alignment.value}">
						<xsl:value-of select="$enddate"/> 
					</fo:inline>

				</fo:block>
		  </xsl:if>
	  </xsl:if>
	</xsl:template>

<!--==============================================================-->	
<!-- key: postcode city / country -->
<!--==============================================================-->

	<xsl:template name="key-postcode-city-country" >
		<xsl:param name = "key"/>
	  <xsl:param name = "postcode"/>
	  <xsl:param name = "city"/>
		<xsl:param name = "country"/>

	  <xsl:if test="$city"> <!--  use only city for check, this seems the most probable input -->
			<fo:block margin-left="{$margin.left.text}"
								font-size="{$font.size.standard}" 
								margin-top="{$margin.top.main}">

				<fo:inline font-weight="{$font.weight.key}" 
										text-align="{$text.alignment.key}">
					<xsl:value-of select="$key"/> 
				</fo:inline>

				<xsl:text>:</xsl:text>

				<xsl:if test="$postcode"> 
					<xsl:text> </xsl:text>
					<fo:inline font-weight="{$font.weight.value}" 
											text-align="{$text.alignment.value}">
						<xsl:value-of select="$postcode"/> 
					</fo:inline>
			  </xsl:if>

				<xsl:text> </xsl:text>

				<fo:inline font-weight="{$font.weight.value}" 
										text-align="{$text.alignment.value}">
					<xsl:value-of select="$city"/> 
				</fo:inline>

				<xsl:if test="$country">
					<xsl:text> / </xsl:text>
					<fo:inline font-weight="{$font.weight.value}" 
											text-align="{$text.alignment.value}">
						<xsl:value-of select="$country"/> 
					</fo:inline>
			  </xsl:if>

			</fo:block>

	  </xsl:if>
	</xsl:template>

<!--==============================================================-->	
<!-- key: name degree (gender) -->
<!--==============================================================-->

	<xsl:template name="key-name-degree-gender" >
		<xsl:param name = "key"/>
	  <xsl:param name = "name"/>
	  <xsl:param name = "degree"/>
		<xsl:param name = "gender"/>

	  <xsl:if test="$name"> <!--  use only name for check which is the most important input -->
			<fo:block margin-left="{$margin.left.text}"
								font-size="{$font.size.standard}" 
								margin-top="{$margin.top.main}">

				<fo:inline font-weight="{$font.weight.key}" 
										text-align="{$text.alignment.key}">
					<xsl:value-of select="$key"/> 
				</fo:inline>

				<xsl:text>: </xsl:text>

				<fo:inline font-weight="{$font.weight.value}" 
										text-align="{$text.alignment.value}">
					<xsl:value-of select="$name"/> 
				</fo:inline>

				<xsl:if test="$degree">
				  <xsl:text>, </xsl:text>

					<fo:inline font-weight="{$font.weight.value}" 
											text-align="{$text.alignment.value}">
						<xsl:value-of select="$degree"/> 
					</fo:inline>
			  </xsl:if>

				<xsl:if test="$gender">
					<xsl:text> </xsl:text>
					<fo:inline font-weight="{$font.weight.value}" 
											text-align="{$text.alignment.value}">
						<xsl:value-of select="$gender"/> 
					</fo:inline>
					<xsl:text></xsl:text>
			  </xsl:if>

			</fo:block>

	  </xsl:if>
	</xsl:template>

<!--==============================================================-->	
<!-- key: value list separated by commas -->
<!--==============================================================-->

	<xsl:template name="key-value-comma-list" >
		<xsl:param name = "key"/>
	  <xsl:param name = "value"/>

	  <xsl:if test="$value">
		  <fo:block margin-left="{$margin.left.text}" 
								font-size="{$font.size.standard}" 
			          margin-top="{$margin.top.main}">

				<fo:inline font-weight="{$font.weight.key}" 
									 text-align="{$text.alignment.key}"  >
					<xsl:value-of select="$key"/> 
				</fo:inline>

				<xsl:text>: </xsl:text>

				<fo:inline font-weight="{$font.weight.value}" 
									 text-align="{$text.alignment.value}">
					<xsl:for-each select="$value">
					  <xsl:value-of select="current()"/>
					  <xsl:if test="position() != last()">, </xsl:if> <!-- do only this for the last item -->
					</xsl:for-each>
				</fo:inline>

			</fo:block>
		</xsl:if>
	</xsl:template>	

<!--==============================================================-->	
<!-- key: every value in a separated line -->
<!--==============================================================-->

	<xsl:template name="key-value-line-list" >
		<xsl:param name = "key"/>
	  <xsl:param name = "value"/>

		<xsl:if test="$value"> <!-- do only if there are values -->

			<fo:block margin-left="{$margin.left.text}"
								font-size="{$font.size.standard}" 
			          margin-top="{$margin.top.main}">
				<fo:inline font-weight="{$font.weight.key}" 
									 text-align="{$text.alignment.key}">
					<xsl:value-of select="$key"/> 
				</fo:inline>

				<xsl:text>: </xsl:text>
			</fo:block>

			<fo:list-block margin-left="{$margin.left.text}"
			               font-size="{$font.size.standard}"
										 margin-top="{$margin.top.main}">
				<xsl:for-each select="$value">
					<fo:list-item>

						<fo:list-item-label>      <!-- for whatever reason there is no space between label and body --> 
							<fo:block></fo:block>   <!-- so the label is skipped here and moved to the body -->  
						</fo:list-item-label>
				
						<fo:list-item-body font-weight="{$font.weight.value}" 
															 text-align="{$text.alignment.value}">
							<fo:block>- <xsl:value-of select="current()"/></fo:block>
						</fo:list-item-body>

				  </fo:list-item>

			  </xsl:for-each>
		  </fo:list-block>
	  </xsl:if>

  </xsl:template>	

<!--==============================================================-->
<!-- table with title (first row) and description (second row) -->
<!--==============================================================-->

	<xsl:template name="table-title-description">
		<xsl:param name = "entity"/>
		<xsl:param name = "entityIndex"/>
	
		<xsl:if test="$entity">
	
			<xsl:for-each select="$entityIndex">
	
				<fo:block	margin-top="{$margin.top.main}"
									padding-before="{$padding.before.border}"
									padding-after="{$padding.after.border}"
									padding-start="{$padding.start.border}"
									padding-end="{$padding.end.border}">
	
					<fo:table table-layout="fixed">
			
						<!-- define the table columns -->
						<fo:table-column column-width="proportional-column-width(1)"/>	<!-- this construct is used to align the table in a centred way -->							
						<fo:table-column column-width="{$width.table}"/> 
						<fo:table-column column-width="proportional-column-width(1)"/>  <!-- this is also part of central alignment approach --> 
																																					<!-- in addition the columns need to be numbered, omitting the first and last column --> 
						<!-- table header -->
						<fo:table-header>
							<fo:table-cell column-number="2" 
														border-width="${width.border}"
														border-color="{$colour.border}"
														border-style="{$style.border}">
								<fo:block text-align="left" 
								          font-size="{$font.size.key-value}"
													font-weight="{$font.weight.key-value}"
													margin-top="{$margin.top.main}"
													padding-before="{$padding.before.border}"
													padding-after="{$padding.after.border}"
													padding-start="{$padding.start.border}"
													padding-end="{$padding.end.border}"
													margin-left="{$margin.left.border}"
													margin-right="{$margin.right.border}">
									<xsl:value-of select="cerif:Title" />
								</fo:block>
							</fo:table-cell>
						</fo:table-header>
	
						<!-- table body -->
						<fo:table-body>					
								<fo:table-row>
	
									<fo:table-cell column-number="2"
																border-width="${width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}">
										<fo:block text-align="left" 
															font-size="{$font.size.standard}"
															font-weight="{$font.weight.value}"
															margin-top="{$margin.top.main}"
															padding-before="{$padding.before.border}"
															padding-after="{$padding.after.border}"
															padding-start="{$padding.start.border}"
															padding-end="{$padding.end.border}"
															margin-left="{$margin.left.border}"
															margin-right="{$margin.right.border}">
											<xsl:value-of select="cerif:Description" />
										</fo:block>
									</fo:table-cell>
	
								</fo:table-row>
						</fo:table-body>				
	
					</fo:table>								
	
				</fo:block> 		
	
			</xsl:for-each> 
	
		</xsl:if>
	
	</xsl:template>

</xsl:stylesheet>


