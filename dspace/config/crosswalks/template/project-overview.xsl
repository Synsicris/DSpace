<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1"
								xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
								xmlns:fo="http://www.w3.org/1999/XSL/Format"
								xmlns:cerif="https://www.openaire.eu/cerif-profile/1.1/"
								exclude-result-prefixes="fo">

<!--########################################################################-->
<!-- VARIABLES -->	
<!--########################################################################-->

<!-- paths -->	
<!-- NOTE: path needs to be given with single hyphens otherwise the path is interpreted as XPath element --> 
<xsl:param name="imageDirectory" select="'/opt/dspace/dspace-syn7/install/config/crosswalks/template'"/>

<!-- font sizes -->	
<xsl:param name="titleFontSize" select="'24pt'"/>
<xsl:param name="sectionTitleFontSize" select="'14pt'"/>
<xsl:param name="subSectionTitleFontSize" select="'12pt'"/>
<xsl:param name="subSubSectionTitleFontSize" select="'11pt'"/>
<xsl:param name="standardFontSize" select="'10pt'"/>
<xsl:param name="keyValueFontSize" select="'13pt'"/> 

<!-- font weigths -->
<xsl:param name="titleFontWeight" select="'bold'"/>
<xsl:param name="keyFontWeight" select="'bold'"/>
<xsl:param name="valueFontWeight" select="'normal'"/> 
<xsl:param name="keyValueFontWeight" select="'bold'"/> 

<!-- alignments -->
<xsl:param name="titleTextAlignment" select="'left'"/>
<xsl:param name="keyTextAlignment" select="'left'"/>
<xsl:param name="valueTextAlignment" select="'left'"/>

<!-- margins -->	
<xsl:param name="textMarginLeft" select="'3mm'"/>
<xsl:param name="titleMarginTop" select="'5mm'"/>	
<xsl:param name="mainMarginTop" select="'2mm'"/>
<xsl:param name="sectionMarginTop" select="'10mm'"/>
<xsl:param name="sectionMarginBottom" select="'-3mm'"/>
<xsl:param name="borderMarginLeft" select="'3mm'"/>
<xsl:param name="borderMarginRight" select="'3mm'"/>
<xsl:param name="gapMarginTop" select="'5mm'"/>

<!-- paddings -->	
<xsl:param name="borderPaddingBefore" select="'2mm'"/>
<xsl:param name="borderPaddingAfter" select="'2mm'"/>
<xsl:param name="borderPaddingStart" select="'3mm'"/>
<xsl:param name="borderPaddingEnd" select="'3mm'"/>

<!-- borders -->
<xsl:param name="borderWidth" select="'0.3mm'"/>
<xsl:param name="borderColour" select="'#808080'"/>
<xsl:param name="borderStyle" select="'solid'"/>

<!-- rulers -->
<xsl:param name="rulerLength" select="'100%'"/>

<!-- CURRENT PROBLEMS AND QUESTIONS -->	
<!-- so far relative paths are not correctly handled, that is why absolute paths are given -->
<!-- how to handle multi-language approaches? using variables? -->

<!--########################################################################-->
<!-- MAIN PAGE -->	
<!--########################################################################-->

	<xsl:template match="cerif:Project">	
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

		<!-- LAYOUT MASTER SET-->
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

		<!-- PAGE SEQUENCE -->
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

				  <!-- project title -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="cerif:Title"/>
						<xsl:with-param name="fontSize" select="$titleFontSize"/>
					</xsl:call-template>

				  <!-- section title - basics -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'Vorhabensbeschreibung / Basisbericht'"/>
						<xsl:with-param name="fontSize" select="$sectionTitleFontSize"/>
					</xsl:call-template>

					<!-- contents -->
					<xsl:call-template name="project"/>
					<xsl:call-template name="projectpartner"/>
					
					<!-- new page -->
          <fo:block break-after='page'/>

				  <!-- section title - aims -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'1 Ziele'"/>
						<xsl:with-param name="fontSize" select="$sectionTitleFontSize"/>
					</xsl:call-template>

				  <!-- sub section title - overall aim of the project -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'1.1	Gesamtziel des Vorhabens'"/>
						<xsl:with-param name="fontSize" select="$subSectionTitleFontSize"/>
					</xsl:call-template>

					<xsl:call-template name="ipw"/>

				  <!-- sub section title - scientific and techical aims of the project -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'1.2	Wissenschaftliche und/oder technische Arbeitsziele des Vorhabens'"/>
						<xsl:with-param name="fontSize" select="$subSectionTitleFontSize"/>
					</xsl:call-template>					

					<xsl:call-template name="projectobjective"/>

				  <!-- sub section title - work aims regarding interaction -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'1.3	Arbeitsziele zur Interaktion'"/>
						<xsl:with-param name="fontSize" select="$subSectionTitleFontSize"/>
					</xsl:call-template>	

					<xsl:call-template name="iaobjective"/>

				  <!-- sub section title - relation of the project to the objectives of the funding policy  -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'1.4	Bezug des Vorhabens zu den förderpolitischen Zielen'"/>
						<xsl:with-param name="fontSize" select="$subSectionTitleFontSize"/>
					</xsl:call-template>	
			
					<!-- new page -->
          <fo:block break-after='page'/>

          <!-- section title - background -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'2 Hintegrund'"/>
						<xsl:with-param name="fontSize" select="$sectionTitleFontSize"/>
					</xsl:call-template>

				  <!-- sub section title - state of the art in science and technology / previous works -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'2.1	Stand der Wissenschaft und Technik / Bisherige Arbeiten'"/>
						<xsl:with-param name="fontSize" select="$subSectionTitleFontSize"/>
					</xsl:call-template>

					<xsl:call-template name="stateofart"/>

					<!-- new page -->
          <fo:block break-after='page'/>

          <!-- section title - working plan -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'3	Ausführliche Beschreibung des Arbeitsplans'"/>
						<xsl:with-param name="fontSize" select="$sectionTitleFontSize"/>
					</xsl:call-template>

				  <!-- sub section title - resource planning of the project -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'3.1	Vorhabenbezogene Ressourcenplanung'"/>
						<xsl:with-param name="fontSize" select="$subSectionTitleFontSize"/>
					</xsl:call-template>

					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'3.1.1	Überblick Arbeitsplanung'"/>
						<xsl:with-param name="fontSize" select="$subSubSectionTitleFontSize"/>
					</xsl:call-template>
			
					<xsl:call-template name="wpoverview"/>
					
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'3.1.2	Arbeitspakete und Arbeiten'"/>
						<xsl:with-param name="fontSize" select="$subSubSectionTitleFontSize"/>
					</xsl:call-template>

					<xsl:call-template name="wp"/>

					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'3.1.3	Forschungsarbeiten'"/>
						<xsl:with-param name="fontSize" select="$subSubSectionTitleFontSize"/>
					</xsl:call-template>

					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'3.1.4	Arbeiten zur Interaktion und Transfer'"/>
						<xsl:with-param name="fontSize" select="$subSubSectionTitleFontSize"/>
					</xsl:call-template>

					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'3.1.5 Kooperationspartner und Akteursgruppen, die adressiert werden'"/>
						<xsl:with-param name="fontSize" select="$subSubSectionTitleFontSize"/>
					</xsl:call-template>

				  <!-- sub section title - milestone planning -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'3.2	Meilensteinplan'"/>
						<xsl:with-param name="fontSize" select="$subSectionTitleFontSize"/>
					</xsl:call-template>

					<xsl:call-template name="milestone"/>

				  <!-- sub section title - in-depth material and methods -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'3.3	Vertiefung Material und Methoden'"/>
						<xsl:with-param name="fontSize" select="$subSectionTitleFontSize"/>
					</xsl:call-template>

					<!-- new page -->
          <fo:block break-after='page'/>

          <!-- section title - exploitation and impact -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'4	Verwertung und Wirkung'"/>
						<xsl:with-param name="fontSize" select="$sectionTitleFontSize"/>
					</xsl:call-template>

				  <!-- sub section title - overview -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'4.1	Überblick'"/>
						<xsl:with-param name="fontSize" select="$subSectionTitleFontSize"/>
					</xsl:call-template>

				  <!-- sub section title - details of exploitation and impact of the project -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'4.2	Details zu Verwertung und Wirkung des Projektes'"/>
						<xsl:with-param name="fontSize" select="$subSectionTitleFontSize"/>
					</xsl:call-template>

				  <!-- sub section title - application possibilities -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'4.3	Anwendung(smöglichkeiten)'"/>
						<xsl:with-param name="fontSize" select="$subSectionTitleFontSize"/>
					</xsl:call-template>

				  <!-- sub section title - social impact and reflections of the project -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'4.4	Gesellschaftliche Wirkungen und Reflexionen zum Projekt'"/>
						<xsl:with-param name="fontSize" select="$subSectionTitleFontSize"/>
					</xsl:call-template>

				  <!-- sub section title - exploitation plans of the individual project partners -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'4.5	Verwertungspläne der einzelnen Projektpartner'"/>
						<xsl:with-param name="fontSize" select="$subSectionTitleFontSize"/>
					</xsl:call-template>					

					<!-- new page -->
          <fo:block break-after='page'/>

          <!-- section title - work distribution/collaboration with third parties -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'5	Arbeitsteilung/Zusammenarbeit mit Dritten'"/>
						<xsl:with-param name="fontSize" select="$sectionTitleFontSize"/>
					</xsl:call-template>

				  <!-- sub section title - project partners (additional informations) -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'5.1	Projektpartner'"/>
						<xsl:with-param name="fontSize" select="$subSectionTitleFontSize"/>
					</xsl:call-template>		

				  <!-- sub section title - coorperation partners -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'5.2	Kooperationspartner'"/>
						<xsl:with-param name="fontSize" select="$subSectionTitleFontSize"/>
					</xsl:call-template>		

				  <!-- sub section title - relevant stakeholder groups of the project -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'5.3	Relevante Akteursgruppen des Projektes'"/>
						<xsl:with-param name="fontSize" select="$subSectionTitleFontSize"/>
					</xsl:call-template>

					<!-- new page -->
          <fo:block break-after='page'/>

          <!-- section title - necessity of the grant -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'6	Notwendigkeit der Zuwendung'"/>
						<xsl:with-param name="fontSize" select="$sectionTitleFontSize"/>
					</xsl:call-template>

				</fo:flow>

			</fo:page-sequence>

		</fo:root>
	</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE PROJECT ENTITY -->	
<!--########################################################################-->

	<xsl:template name="project" >
							
		<xsl:call-template name="key-value-single">
			<xsl:with-param name="key" select="'Akronym'"/>
			<xsl:with-param name="value" select="cerif:Acronym"/>
		</xsl:call-template>

		<xsl:call-template name="key-period">
			<xsl:with-param name="key" select="'Laufzeit'"/>
			<xsl:with-param name="startdate" select="cerif:StartDate"/>
			<xsl:with-param name="enddate" select="cerif:EndDate"/>
		</xsl:call-template>
	
		<xsl:call-template name="key-value-single">
			<xsl:with-param name="key" select="'Dauer in Monaten'"/>
			<xsl:with-param name="value" select="cerif:Duration"/>
		</xsl:call-template>

		<xsl:call-template name="key-value-single">
			<xsl:with-param name="key" select="'Förderbereich/Bekanntmachung'"/>
			<xsl:with-param name="value" select="cerif:FundingCall"/>
		</xsl:call-template>

		<xsl:call-template name="key-value-single">
			<xsl:with-param name="key" select="'Fördermaßnahme/Förderprogramm'"/>
			<xsl:with-param name="value" select="cerif:FundingProgramme"/>
		</xsl:call-template>			
	
	</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE PROJECT PARTNER ENTITY -->	
<!--########################################################################-->

	<xsl:template name="projectpartner">

		<xsl:if test="cerif:ProjectPartner">

			<xsl:call-template name="vertical-gap">  <!-- make a small gap first to indicate a separation from the items above-->
				<xsl:with-param name="margin" select="$gapMarginTop"/>
		  </xsl:call-template>

			<xsl:call-template name="key-only">  <!-- partner key -->
				<xsl:with-param name="key" select="'Partner'"/>
		  </xsl:call-template>	

			<xsl:for-each select="cerif:ProjectPartner/cerif:Index">

				<fo:block border-width="{$borderWidth}"
									border-color="{$borderColour}"
									border-style="{$borderStyle}"
									margin-top="{$mainMarginTop}"
									margin-left="{$borderMarginLeft}"
									margin-right="{$borderMarginRight}"
									padding-before="{$borderPaddingBefore}"
									padding-after="{$borderPaddingAfter}"
									padding-start="{$borderPaddingStart}"
									padding-end="{$borderPaddingEnd}">
									
					<xsl:call-template name="value-single">
						<xsl:with-param name="value" select="cerif:Acronym"/>
						<xsl:with-param name="fontSize" select="$keyValueFontSize"/>
						<xsl:with-param name="fontWeight" select="$keyValueFontWeight"/>
					</xsl:call-template>									

					<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="'Ausführende Stelle im easy-online-Antrag'"/>
						<xsl:with-param name="value" select="cerif:EasyOnlineImport"/>
				  </xsl:call-template>						

					<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="'Tätigkeitsbereich der Organisationseinheit'"/>
						<xsl:with-param name="value" select="cerif:SectorType"/>
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
						<xsl:with-param name="margin" select="$gapMarginTop"/>
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
						<xsl:with-param name="key" select="'PLZ / Stadt / Land'"/>
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
						<xsl:with-param name="margin" select="$gapMarginTop"/>
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
<!-- TEMPLATE FOR THE PROJECT OBJECTIVE ENTITY -->	
<!--########################################################################-->

<xsl:template name="projectobjective">

<xsl:if test="cerif:ProjectObjective">

	<xsl:for-each select="cerif:ProjectObjective/cerif:Index">

		<fo:block>
		<!--	
			        border-width="{$borderWidth}"
							border-color="{$borderColour}"
							border-style="{$borderStyle}"
							margin-top="{$mainMarginTop}"
							margin-left="{$borderMarginLeft}"
							margin-right="{$borderMarginRight}"
							padding-before="{$borderPaddingBefore}"
							padding-after="{$borderPaddingAfter}"
							padding-start="{$borderPaddingStart}"
							padding-end="{$borderPaddingEnd}">

		-->					

      <fo:table table-layout="fixed">
	
				<fo:table-column column-width="100%"/> <!-- define the columns -->				

				<fo:table-header>
					<fo:table-cell column-number="1" border-width="thin"  border-style="solid">
						<fo:block text-align="left" font-weight="bold">
							<xsl:value-of select="cerif:Title" />
						</fo:block>
					</fo:table-cell>
				</fo:table-header>

				<fo:table-body>					
						<fo:table-row>

							<fo:table-cell column-number="1" border-width="thin" border-style="solid">
								<fo:block text-align="left">
									<xsl:value-of select="cerif:Description" />
								</fo:block>
						  </fo:table-cell>

						</fo:table-row>
				</fo:table-body>				

			</fo:table>								

		<!--	
			<xsl:call-template name="title-section-counter"> 
				<xsl:with-param name="section" select="'1.2.'"/>				
				<xsl:with-param name="counter" select="position()"/>
				<xsl:with-param name="title" select="cerif:Title"/>
				<xsl:with-param name="fontSize" select="$standardFontSize"/>
				<xsl:with-param name="rulerLength" select="$rulerLength"/>
			</xsl:call-template>									

			<xsl:call-template name="value-single">
				<xsl:with-param name="value" select="cerif:Description"/>
				<xsl:with-param name="fontSize" select="$standardFontSize"/>
				<xsl:with-param name="fontWeight" select="$valueFontWeight"/>
			</xsl:call-template>					
    -->

		</fo:block> 		

	</xsl:for-each> 

</xsl:if>

</xsl:template>


<!--########################################################################-->
<!-- TEMPLATE FOR THE IMPACT PATHWAY -->	
<!--########################################################################-->

  <xsl:template name="ipw" >

	  <xsl:if test="cerif:ImpactPathway">

		  <xsl:for-each select="cerif:ImpactPathway/cerif:Index">

			  <xsl:call-template name="title-section-counter"> 
				<xsl:with-param name="section" select="'1.1.'"/>				
				  <xsl:with-param name="counter" select="position()"/>
					<xsl:with-param name="title" select="' Beschreibung des Gesamtziels des Vorhabens über den Impact Pathway'"/>
					<xsl:with-param name="fontSize" select="$subSubSectionTitleFontSize"/>
			  </xsl:call-template>			

				<xsl:call-template name="value-single">
					<xsl:with-param name="value" select="cerif:Description"/>
					<xsl:with-param name="fontSize" select="$standardFontSize"/>
					<xsl:with-param name="fontWeight" select="$valueFontWeight"/>
				</xsl:call-template>

		  </xsl:for-each>

	  </xsl:if>
		
  </xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE STATE OF ART/SCIENCE ENTITY -->	
<!--########################################################################-->

  <xsl:template name="stateofart">

		<xsl:if test="cerif:StateOfArt">

			<xsl:call-template name="key-only">
				<xsl:with-param name="key" select="'Stand der Wissenschaft und Technik'"/>
			</xsl:call-template>	

			<xsl:for-each select="cerif:StateOfArt/cerif:Index">

				<fo:block border-width="{$borderWidth}"
									border-color="{$borderColour}"
									border-style="{$borderStyle}"
									margin-top="{$mainMarginTop}"
									margin-left="{$borderMarginLeft}"
									margin-right="{$borderMarginRight}"
									padding-before="{$borderPaddingBefore}"
									padding-after="{$borderPaddingAfter}"
									padding-start="{$borderPaddingStart}"
									padding-end="{$borderPaddingEnd}">

						<xsl:call-template name="value-single">
							<xsl:with-param name="value" select="cerif:Title"/>
							<xsl:with-param name="fontSize" select="$keyValueFontSize"/>
							<xsl:with-param name="fontWeight" select="$keyValueFontWeight"/>
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
<!-- TEMPLATE FOR THE INTERACTION OBJECTIVE ENTITY -->	
<!--########################################################################-->

  <xsl:template name="iaobjective">

		<xsl:if test="cerif:InteractionObjective">

			<xsl:call-template name="key-only">
				<xsl:with-param name="key" select="'Akteursgruppen'"/>
			</xsl:call-template>	

			<xsl:for-each select="cerif:InteractionObjective/cerif:Index">

				<fo:block border-width="{$borderWidth}"
									border-color="{$borderColour}"
									border-style="{$borderStyle}"
									margin-top="{$mainMarginTop}"
									margin-left="{$borderMarginLeft}"
									margin-right="{$borderMarginRight}"
									padding-before="{$borderPaddingBefore}"
									padding-after="{$borderPaddingAfter}"
									padding-start="{$borderPaddingStart}"
									padding-end="{$borderPaddingEnd}">

					<xsl:call-template name="value-single">
						<xsl:with-param name="value" select="cerif:Title"/>
						<xsl:with-param name="fontSize" select="$keyValueFontSize"/>
						<xsl:with-param name="fontWeight" select="$keyValueFontWeight"/>
					</xsl:call-template>	

					<xsl:call-template name="key-value-comma-list">
					  <xsl:with-param name="key" select="'Beschreibung'"/>
					  <xsl:with-param name="value" select="cerif:Description"/>
				  </xsl:call-template>

			  </fo:block> 		

			</xsl:for-each> -->
	
		</xsl:if>
	
	</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE MILESTONES -->	
<!--########################################################################-->

<xsl:template name="milestone">

<xsl:if test="cerif:Milestone">

	<xsl:for-each select="cerif:Milestone/cerif:Index">

		<fo:block border-width="{$borderWidth}"
							border-color="{$borderColour}"
							border-style="{$borderStyle}"
							margin-top="{$mainMarginTop}"
							margin-left="{$borderMarginLeft}"
							margin-right="{$borderMarginRight}"
							padding-before="{$borderPaddingBefore}"
							padding-after="{$borderPaddingAfter}"
							padding-start="{$borderPaddingStart}"
							padding-end="{$borderPaddingEnd}">

				<xsl:call-template name="value-single">
					<xsl:with-param name="value" select="cerif:Title"/>
					<xsl:with-param name="fontSize" select="$keyValueFontSize"/>
					<xsl:with-param name="fontWeight" select="$keyValueFontWeight"/>
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
<!-- TEMPLATE FOR THE WorkingPlanOverview -->	
<!--########################################################################-->

<xsl:template name="wpoverview">

			<fo:block border-width="{$borderWidth}"
					border-color="{$borderColour}"
					border-style="{$borderStyle}"
					margin-top="{$mainMarginTop}"
					margin-left="{$borderMarginLeft}"
					margin-right="{$borderMarginRight}"
					padding-before="{$borderPaddingBefore}"
					padding-after="{$borderPaddingAfter}"
					padding-start="{$borderPaddingStart}"
					padding-end="{$borderPaddingEnd}">
			
					<xsl:call-template name="key-value-line-list" >				
							<xsl:with-param name = "key" select="'Arbeitspakete'"/>
							<xsl:with-param name = "value" select="cerif:WorkPackage/cerif:Index/cerif:Title" />
							<xsl:with-param name="fontSize" select="$keyValueFontSize"/>
						<xsl:with-param name="fontWeight" select="$keyValueFontWeight"/>
					</xsl:call-template>

			</fo:block>

			<fo:block border-width="{$borderWidth}"
						border-color="{$borderColour}"
						border-style="{$borderStyle}"
						margin-top="{$mainMarginTop}"
						margin-left="{$borderMarginLeft}"
						margin-right="{$borderMarginRight}"
						padding-before="{$borderPaddingBefore}"
						padding-after="{$borderPaddingAfter}"
						padding-start="{$borderPaddingStart}"
						padding-end="{$borderPaddingEnd}">

					<xsl:call-template name="key-value-line-list" >
							<xsl:with-param name = "key" select="'Aufgaben'"/>
							<xsl:with-param name = "value" select="cerif:Task/cerif:Index/cerif:Title" />
							<xsl:with-param name="fontSize" select="$keyValueFontSize"/>
						<xsl:with-param name="fontWeight" select="$keyValueFontWeight"/>
					</xsl:call-template>

			</fo:block>	

			<fo:block border-width="{$borderWidth}"
						border-color="{$borderColour}"
						border-style="{$borderStyle}"
						margin-top="{$mainMarginTop}"
						margin-left="{$borderMarginLeft}"
						margin-right="{$borderMarginRight}"
						padding-before="{$borderPaddingBefore}"
						padding-after="{$borderPaddingAfter}"
						padding-start="{$borderPaddingStart}"
						padding-end="{$borderPaddingEnd}">

					<xsl:call-template name="key-value-line-list" >
						<xsl:with-param name = "key" select="'Veranstaltungen'"/>
						<xsl:with-param name = "value" select="cerif:Event/cerif:Index/cerif:Title" />
						<xsl:with-param name="fontSize" select="$keyValueFontSize"/>
					<xsl:with-param name="fontWeight" select="$keyValueFontWeight"/>
					</xsl:call-template>

			</fo:block>	

			<fo:block border-width="{$borderWidth}"
						border-color="{$borderColour}"
						border-style="{$borderStyle}"
						margin-top="{$mainMarginTop}"
						margin-left="{$borderMarginLeft}"
						margin-right="{$borderMarginRight}"
						padding-before="{$borderPaddingBefore}"
						padding-after="{$borderPaddingAfter}"
						padding-start="{$borderPaddingStart}"
						padding-end="{$borderPaddingEnd}">

					<xsl:call-template name="key-value-line-list" >
						<xsl:with-param name = "key" select="'Projekttreffen'"/>
						<xsl:with-param name = "value" select="cerif:ProcessEvent/cerif:Index/cerif:Title" />
						<xsl:with-param name="fontSize" select="$keyValueFontSize"/>
					<xsl:with-param name="fontWeight" select="$keyValueFontWeight"/>
					</xsl:call-template>

				</fo:block>	

				<fo:block border-width="{$borderWidth}"
							border-color="{$borderColour}"
							border-style="{$borderStyle}"
							margin-top="{$mainMarginTop}"
							margin-left="{$borderMarginLeft}"
							margin-right="{$borderMarginRight}"
							padding-before="{$borderPaddingBefore}"
							padding-after="{$borderPaddingAfter}"
							padding-start="{$borderPaddingStart}"
							padding-end="{$borderPaddingEnd}">

					<xsl:call-template name="key-value-line-list" >
						<xsl:with-param name = "key" select="'Geplante Veröffentlichungen'"/>
						<xsl:with-param name = "value" select="cerif:PlannedPublication/cerif:Index/cerif:Title" />
						<xsl:with-param name="fontSize" select="$keyValueFontSize"/>
					<xsl:with-param name="fontWeight" select="$keyValueFontWeight"/>
					</xsl:call-template>

				</fo:block>	

				<fo:block border-width="{$borderWidth}"
							border-color="{$borderColour}"
							border-style="{$borderStyle}"
							margin-top="{$mainMarginTop}"
							margin-left="{$borderMarginLeft}"
							margin-right="{$borderMarginRight}"
							padding-before="{$borderPaddingBefore}"
							padding-after="{$borderPaddingAfter}"
							padding-start="{$borderPaddingStart}"
							padding-end="{$borderPaddingEnd}">

					<xsl:call-template name="key-value-line-list" >
						<xsl:with-param name = "key" select="'Gegenstände/Materialien'"/>
						<xsl:with-param name = "value" select="cerif:PhysicalObject/cerif:Index/cerif:Title" />
						<xsl:with-param name="fontSize" select="$keyValueFontSize"/>
					<xsl:with-param name="fontWeight" select="$keyValueFontWeight"/>
					</xsl:call-template>

 				</fo:block>	

</xsl:template>


<!--########################################################################-->
<!-- TEMPLATE FOR THE WorkPackages -->	
<!--########################################################################-->

<xsl:template name="wp">

<xsl:if test="cerif:WorkPackage">

	<xsl:for-each select="cerif:WorkPackage/cerif:Index">

		<fo:block border-width="{$borderWidth}"
							border-color="{$borderColour}"
							border-style="{$borderStyle}"
							margin-top="{$mainMarginTop}"
							margin-left="{$borderMarginLeft}"
							margin-right="{$borderMarginRight}"
							padding-before="{$borderPaddingBefore}"
							padding-after="{$borderPaddingAfter}"
							padding-start="{$borderPaddingStart}"
							padding-end="{$borderPaddingEnd}">

							<fo:table table-layout="fixed">

							<fo:table-column column-width="100%"/> <!-- define the columns -->				
							
							<fo:table-header>
								<fo:table-cell column-number="1" border-width="thin"  border-style="solid">
									<fo:block text-align="left" font-weight="bold">
										<xsl:value-of select="cerif:Title" />
									</fo:block>
								</fo:table-cell>
							</fo:table-header>
							
							<fo:table-body>					
									
									<fo:table-row>							

										<fo:table-cell column-number="1" border-width="thin" border-style="solid">
												<fo:block text-align="left">
												<xsl:call-template name="title">
													<xsl:with-param name="title" select="'Verantwortlicher Partner'"/>
													<xsl:with-param name="fontSize" select="$subSubSectionTitleFontSize"/>
												</xsl:call-template>
												<xsl:value-of select="cerif:ProjectPartner/cerif:Index/cerif:Import/cerif:OrganisationName" />
												</fo:block>

										</fo:table-cell>
													 
									</fo:table-row>
														
									<fo:table-row>
							
										<fo:table-cell column-number="1" border-width="thin" border-style="solid">
											<fo:block text-align="left">
												<xsl:call-template name="title">
													<xsl:with-param name="title" select="'Beschreibung des Arbeitspaketes'"/>
													<xsl:with-param name="fontSize" select="$subSubSectionTitleFontSize"/>
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
												<xsl:with-param name="fontSize" select="$subSubSectionTitleFontSize"/>
											</xsl:call-template>
											<xsl:value-of select="cerif:Requirement" />
										</fo:block>
									</fo:table-cell>

									</fo:table-row>

							</fo:table-body>				
							
							</fo:table>	

	  </fo:block> 		

	</xsl:for-each> -->

</xsl:if>

</xsl:template>	

<!--########################################################################-->
<!-- GENERAL TEMPLATES -->	
<!--########################################################################-->

	<!-- vertical gap -->	
	<xsl:template name="vertical-gap" >
		<xsl:param name = "margin"/>

		<fo:block margin-left="{$textMarginLeft}"
							margin-top="{$margin}">
		</fo:block>

	</xsl:template>

	<!-- title -->	
	<xsl:template name="title" >
		<xsl:param name = "title"/>
		<xsl:param name = "fontSize"/>

		<fo:block margin-left="{$textMarginLeft}"
							margin-top="{$titleMarginTop}">
			<fo:inline font-size="{$fontSize}"
								 font-weight="{$titleFontWeight}"
								 text-align="{$titleTextAlignment}">							
				<xsl:value-of select="$title"/>
			</fo:inline>
		</fo:block>

	</xsl:template>

	<!-- section title with counter -->	
	<xsl:template name="title-section-counter" >
		<xsl:param name = "section"/>
		<xsl:param name = "counter"/>
		<xsl:param name = "title"/>
		<xsl:param name = "fontSize"/>
		<xsl:param name = "rulerLength"/>

		<fo:block margin-left="{$textMarginLeft}"
							margin-top="{$titleMarginTop}">

			<xsl:if test="$section">							
				<fo:inline font-size="{$fontSize}"
								   font-weight="{$titleFontWeight}"
								   text-align="{$titleTextAlignment}">							
					<xsl:value-of select="$section"/>
				</fo:inline>
      </xsl:if>

			<xsl:if test="$counter">			
				<fo:inline font-size="{$fontSize}"
								   font-weight="{$titleFontWeight}"
								   text-align="{$titleTextAlignment}">							
					<xsl:value-of select="$counter"/>
				</fo:inline>
      </xsl:if>

			<fo:inline font-size="{$fontSize}"
								 font-weight="{$titleFontWeight}"
								 text-align="{$titleTextAlignment}">							
				<xsl:value-of select="$title"/>
			</fo:inline>

			<xsl:if test="$rulerLength">   
				<fo:block>
					<fo:leader leader-pattern="rule" 
										 leader-length="{$rulerLength}"
										 rule-style="solid" />         
				</fo:block>
      </xsl:if>

		</fo:block>

	</xsl:template>

	<!--  single value -->
	<xsl:template name="value-single" >
	  <xsl:param name = "value"/>
		<xsl:param name = "fontSize"/>
		<xsl:param name = "fontWeight"/>

	  <xsl:if test="$value">
		  <fo:block margin-left="{$textMarginLeft}"
								font-size="{$fontSize}" 
			        	margin-top="{$mainMarginTop}">

				<xsl:choose> 
					<xsl:when test="$fontWeight">								
						<fo:inline font-weight="{$fontWeight}" 
											 text-align="{$valueTextAlignment}">
							<xsl:value-of select="$value"/> 
						</fo:inline>
					</xsl:when>
					<xsl:otherwise>
						<fo:inline font-weight="{$valueFontWeight}" 
											 text-align="{$valueTextAlignment}">
							<xsl:value-of select="$value"/> 
						</fo:inline>
					</xsl:otherwise>
			  </xsl:choose>

			</fo:block>
	  </xsl:if>
	</xsl:template>

	<!-- key: no value -->
	<xsl:template name="key-only" >
	  <xsl:param name = "key"/>

	  <xsl:if test="$key">
		  <fo:block margin-left="{$textMarginLeft}"
								font-size="{$standardFontSize}" 
			        	margin-top="{$mainMarginTop}">

				<fo:inline font-weight="{$keyFontWeight}" 
									 text-align="{$keyTextAlignment}">
					<xsl:value-of select="$key"/> 
				</fo:inline>

				<xsl:text>: </xsl:text>

			</fo:block>
	  </xsl:if>
	</xsl:template>

	<!-- key: single value -->
	<xsl:template name="key-value-single" >
	  <xsl:param name = "key"/>
	  <xsl:param name = "value"/>

	  <xsl:if test="$value">
		  <fo:block margin-left="{$textMarginLeft}"
								font-size="{$standardFontSize}" 
			        	margin-top="{$mainMarginTop}">

				<fo:inline font-weight="{$keyFontWeight}" 
				           text-align="{$keyTextAlignment}">
					<xsl:value-of select="$key"/> 
				</fo:inline>

				<xsl:text>: </xsl:text>

				<fo:inline font-weight="{$valueFontWeight}" 
									 text-align="{$valueTextAlignment}">
					<xsl:value-of select="$value"/> 
				</fo:inline>

			</fo:block>
	  </xsl:if>
	</xsl:template>
	
	<!-- key: period -->
	<xsl:template name="key-period" >
	  <xsl:param name = "key"/>
	  <xsl:param name = "startdate"/>
		<xsl:param name = "enddate"/>

	  <xsl:if test="$startdate">
			<xsl:if test="$enddate">
				<fo:block margin-left="{$textMarginLeft}"
									font-size="{$standardFontSize}" 
									margin-top="{$mainMarginTop}">

					<fo:inline font-weight="{$keyFontWeight}" 
										 text-align="{$keyTextAlignment}">
						<xsl:value-of select="$key"/> 
					</fo:inline>

					<xsl:text>: </xsl:text>

					<fo:inline font-weight="{$valueFontWeight}" 
										 text-align="{$valueTextAlignment}">
						<xsl:value-of select="$startdate"/> 
					</fo:inline>

					<xsl:text> - </xsl:text>

					<fo:inline font-weight="{$valueFontWeight}" 
										 text-align="{$valueTextAlignment}">
						<xsl:value-of select="$enddate"/> 
					</fo:inline>

				</fo:block>
		  </xsl:if>
	  </xsl:if>
	</xsl:template>

	<!-- key: postcode city / country -->
	<xsl:template name="key-postcode-city-country" >
		<xsl:param name = "key"/>
	  <xsl:param name = "postcode"/>
	  <xsl:param name = "city"/>
		<xsl:param name = "country"/>

	  <xsl:if test="$city"> <!--  use only city for check, this seems the most probable input -->
			<fo:block margin-left="{$textMarginLeft}"
								font-size="{$standardFontSize}" 
								margin-top="{$mainMarginTop}">

				<fo:inline font-weight="{$keyFontWeight}" 
										text-align="{$keyTextAlignment}">
					<xsl:value-of select="$key"/> 
				</fo:inline>

				<xsl:text>:</xsl:text>

				<xsl:if test="$postcode"> 
					<xsl:text> </xsl:text>
					<fo:inline font-weight="{$valueFontWeight}" 
											text-align="{$valueTextAlignment}">
						<xsl:value-of select="$postcode"/> 
					</fo:inline>
			  </xsl:if>

				<xsl:text> </xsl:text>

				<fo:inline font-weight="{$valueFontWeight}" 
										text-align="{$valueTextAlignment}">
					<xsl:value-of select="$city"/> 
				</fo:inline>

				<xsl:if test="$country">
					<xsl:text> / </xsl:text>
					<fo:inline font-weight="{$valueFontWeight}" 
											text-align="{$valueTextAlignment}">
						<xsl:value-of select="$country"/> 
					</fo:inline>
			  </xsl:if>

			</fo:block>

	  </xsl:if>
	</xsl:template>

	<!-- key: name degree (gender) -->
	<xsl:template name="key-name-degree-gender" >
		<xsl:param name = "key"/>
	  <xsl:param name = "name"/>
	  <xsl:param name = "degree"/>
		<xsl:param name = "gender"/>

	  <xsl:if test="$name"> <!--  use only name for check which is the most important input -->
			<fo:block margin-left="{$textMarginLeft}"
								font-size="{$standardFontSize}" 
								margin-top="{$mainMarginTop}">

				<fo:inline font-weight="{$keyFontWeight}" 
										text-align="{$keyTextAlignment}">
					<xsl:value-of select="$key"/> 
				</fo:inline>

				<xsl:text>: </xsl:text>

				<fo:inline font-weight="{$valueFontWeight}" 
										text-align="{$valueTextAlignment}">
					<xsl:value-of select="$name"/> 
				</fo:inline>

				<xsl:if test="$degree">
				  <xsl:text>, </xsl:text>

					<fo:inline font-weight="{$valueFontWeight}" 
											text-align="{$valueTextAlignment}">
						<xsl:value-of select="$degree"/> 
					</fo:inline>
			  </xsl:if>

				<xsl:if test="$gender">
					<xsl:text> (</xsl:text>
					<fo:inline font-weight="{$valueFontWeight}" 
											text-align="{$valueTextAlignment}">
						<xsl:value-of select="$gender"/> 
					</fo:inline>
					<xsl:text>)</xsl:text>
			  </xsl:if>

			</fo:block>

	  </xsl:if>
	</xsl:template>

	<!-- key: value list separated by commas -->
	<xsl:template name="key-value-comma-list" >
		<xsl:param name = "key"/>
	  <xsl:param name = "value"/>

	  <xsl:if test="$value">
		  <fo:block margin-left="{$textMarginLeft}" 
								font-size="{$standardFontSize}" 
			          margin-top="{$mainMarginTop}">

				<fo:inline font-weight="{$keyFontWeight}" 
									 text-align="{$keyTextAlignment}"  >
					<xsl:value-of select="$key"/> 
				</fo:inline>

				<xsl:text>: </xsl:text>

				<fo:inline font-weight="{$valueFontWeight}" 
									 text-align="{$valueTextAlignment}">
					<xsl:for-each select="$value">
					  <xsl:value-of select="current()"/>
					  <xsl:if test="position() != last()">, </xsl:if> <!-- do only this for the last item -->
					</xsl:for-each>
				</fo:inline>

			</fo:block>
		</xsl:if>
	</xsl:template>	

	<!-- key: every value in a separated line -->
	<xsl:template name="key-value-line-list" >
		<xsl:param name = "key"/>
	  <xsl:param name = "value"/>

		<xsl:if test="$value"> <!-- do only if there are values -->

			<fo:block margin-left="{$textMarginLeft}"
								font-size="{$standardFontSize}" 
			          margin-top="{$mainMarginTop}">
				<fo:inline font-weight="{$keyFontWeight}" 
									 text-align="{$keyTextAlignment}">
					<xsl:value-of select="$key"/> 
				</fo:inline>

				<xsl:text>: </xsl:text>
			</fo:block>

			<fo:list-block margin-left="{$textMarginLeft}"
			               font-size="{$standardFontSize}"
										 margin-top="{$mainMarginTop}">
				<xsl:for-each select="$value">
					<fo:list-item>

						<fo:list-item-label>      <!-- for whatever reason there is no space between label and body --> 
							<fo:block></fo:block>   <!-- so the label is skipped here and moved to the body -->  
						</fo:list-item-label>
				
						<fo:list-item-body font-weight="{$valueFontWeight}" 
															 text-align="{$valueTextAlignment}">
							<fo:block>- <xsl:value-of select="current()"/></fo:block>
						</fo:list-item-body>

				  </fo:list-item>

			  </xsl:for-each>
		  </fo:list-block>
	  </xsl:if>

  </xsl:template>	

</xsl:stylesheet>