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
<xsl:param name="standardFontSize" select="'10pt'"/> 

<!-- font weigths -->
<xsl:param name="titleFontWeight" select="'bold'"/>
<xsl:param name="labelFontWeight" select="'bold'"/>
<xsl:param name="valueFontWeight" select="'normal'"/> 

<!-- alignments -->
<xsl:param name="titleTextAlignment" select="'left'"/>
<xsl:param name="labelTextAlignment" select="'left'"/>
<xsl:param name="valueTextAlignment" select="'left'"/>

<!-- margins -->	
<xsl:param name="textMarginLeft" select="'3mm'"/>
<xsl:param name="titleMarginTop" select="'5mm'"/>	
<xsl:param name="mainMarginTop" select="'2mm'"/>
<xsl:param name="sectionMarginTop" select="'10mm'"/>
<xsl:param name="sectionMarginBottom" select="'-3mm'"/>
<xsl:param name="borderMarginLeft" select="'3mm'"/>
<xsl:param name="borderMarginRight" select="'3mm'"/>

<!-- paddings -->	
<xsl:param name="borderPaddingBefore" select="'2mm'"/>
<xsl:param name="borderPaddingAfter" select="'2mm'"/>
<xsl:param name="borderPaddingStart" select="'3mm'"/>
<xsl:param name="borderPaddingEnd" select="'3mm'"/>

<!-- borders -->
<xsl:param name="borderWidth" select="'0.5mm'"/>
<xsl:param name="borderColour" select="'#808080'"/>
<xsl:param name="borderStyle" select="'solid'"/>

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
				   -->
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

				  <!-- test area -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'Testbereich'"/>
						<xsl:with-param name="fontSize" select="$sectionTitleFontSize"/>
					</xsl:call-template>

					<!--Tests-->

					<xsl:call-template name="key-value-comma-list">
						<xsl:with-param name="label" select="'Test ExpectedSocietalImpact Direct'"/>
						<xsl:with-param name="values" select="cerif:ExpectedSocietalImpact/cerif:Title"/>
					</xsl:call-template>

					<xsl:call-template name="key-value-line-list">
						<xsl:with-param name="label" select="'Test ExpectedSocietalImpact Inline Group'"/>
						<xsl:with-param name="values" select="cerif:ExpectedSocietalImpact/cerif:Type/cerif:CoreElement"/>
					</xsl:call-template>
			
					<!-- new page -->
          <fo:block break-after='page'/>

				  <!-- section title - basics -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'Vorhabensbeschreibung / Basisbericht'"/>
						<xsl:with-param name="fontSize" select="$sectionTitleFontSize"/>
					</xsl:call-template>

					<xsl:call-template name="project">
					</xsl:call-template>

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

				  <!-- sub section title - scientific and techical aims of the project -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'1.2	Wissenschaftliche und/oder technische Arbeitsziele des Vorhabens'"/>
						<xsl:with-param name="fontSize" select="$subSectionTitleFontSize"/>
					</xsl:call-template>					

				  <!-- sub section title - work aims regarding interaction -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'1.3	Arbeitsziele zur Interaktion'"/>
						<xsl:with-param name="fontSize" select="$subSectionTitleFontSize"/>
					</xsl:call-template>	

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

				  <!-- sub section title - milestone planning -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'3.2	Meilensteinplan'"/>
						<xsl:with-param name="fontSize" select="$subSectionTitleFontSize"/>
					</xsl:call-template>

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
<!-- ENTITY SPECIFIC TEMPLATES -->	
<!--########################################################################-->

	<!-- project -->	
	<xsl:template name = "project" >

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
							
			<xsl:call-template name="key-value-single"> <!-- acronym -->
				<xsl:with-param name="label" select="'Akronym'"/>
				<xsl:with-param name="value" select="cerif:Acronym"/>
			</xsl:call-template>

			<xsl:call-template name="key-period"> <!-- period -->
				<xsl:with-param name="label" select="'Laufzeit'"/>
				<xsl:with-param name="startdate" select="cerif:StartDate"/>
				<xsl:with-param name="enddate" select="cerif:EndDate"/>
			</xsl:call-template>
		
			<xsl:call-template name="key-value-single"> <!-- duration -->
				<xsl:with-param name="label" select="'Dauer in Monaten'"/>
				<xsl:with-param name="value" select="cerif:Duration"/>
			</xsl:call-template>

			<xsl:call-template name="key-value-single"> <!-- funding call -->
				<xsl:with-param name="label" select="'Förderbereich/Bekanntmachung'"/>
				<xsl:with-param name="value" select="cerif:FundingCall"/>
			</xsl:call-template>

			<xsl:call-template name="key-value-single"> <!-- funding programme -->
				<xsl:with-param name="label" select="'Fördermaßnahme/Förderprogramm'"/>
				<xsl:with-param name="value" select="cerif:FundingProgramme"/>
			</xsl:call-template>			

		</fo:block> 		
	
	</xsl:template>

<!--########################################################################-->
<!-- GENERAL TEMPLATES -->	
<!--########################################################################-->

	<!-- title -->	
	<xsl:template name = "title" >
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

	<!-- key: no value -->
	<xsl:template name = "key-only" >
	  <xsl:param name = "label"/>

	  <xsl:if test="$label">
		  <fo:block margin-left="{$textMarginLeft}"
								font-size="{$standardFontSize}" 
			        	margin-top="{$mainMarginTop}">

				<fo:inline font-weight="{$labelFontWeight}" 
									 text-align="{$labelTextAlignment}">
					<xsl:value-of select="$label"/> 
				</fo:inline>

				<xsl:text>: </xsl:text>

			</fo:block>
	  </xsl:if>
	</xsl:template>

	<!-- key: single value -->
	<xsl:template name = "key-value-single" >
	  <xsl:param name = "label"/>
	  <xsl:param name = "value"/>

	  <xsl:if test="$value">
		  <fo:block margin-left="{$textMarginLeft}"
								font-size="{$standardFontSize}" 
			        	margin-top="{$mainMarginTop}">

				<fo:inline font-weight="{$labelFontWeight}" 
				           text-align="{$labelTextAlignment}">
					<xsl:value-of select="$label"/> 
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
	<xsl:template name = "key-period" >
	  <xsl:param name = "label"/>
	  <xsl:param name = "startdate"/>
		<xsl:param name = "enddate"/>

	  <xsl:if test="$startdate">
			<xsl:if test="$enddate">
				<fo:block margin-left="{$textMarginLeft}"
									font-size="{$standardFontSize}" 
									margin-top="{$mainMarginTop}">

					<fo:inline font-weight="{$labelFontWeight}" 
										 text-align="{$labelTextAlignment}">
						<xsl:value-of select="$label"/> 
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

	<!-- key: value list separated by commas -->
	<xsl:template name = "key-value-comma-list" >
		<xsl:param name = "label"/>
	  <xsl:param name = "values"/>

	  <xsl:if test="$values">
		  <fo:block margin-left="{$textMarginLeft}" 
								font-size="{$standardFontSize}" 
			          margin-top="{$mainMarginTop}">

				<fo:inline font-weight="{$labelFontWeight}" 
									 text-align="{$labelTextAlignment}"  >
					<xsl:value-of select="$label"/> 
				</fo:inline>

				<xsl:text>: </xsl:text>

				<fo:inline font-weight="{$valueFontWeight}" 
									 text-align="{$valueTextAlignment}">
					<xsl:for-each select="$values">
					  <xsl:value-of select="current()"/>
					  <xsl:if test="position() != last()">, </xsl:if> <!-- do only this for the last item -->
					</xsl:for-each>
				</fo:inline>

			</fo:block>
		</xsl:if>
	</xsl:template>	

	<!-- key: every value in a separated line -->
	<xsl:template name = "key-value-line-list" >
		<xsl:param name = "label"/>
	  <xsl:param name = "values"/>

		<xsl:if test="$values"> <!-- do only if there are values -->

			<fo:block margin-left="{$textMarginLeft}"
								font-size="{$standardFontSize}" 
			          margin-top="{$mainMarginTop}">
				<fo:inline font-weight="{$labelFontWeight}" 
									 text-align="{$labelTextAlignment}">
					<xsl:value-of select="$label"/> 
				</fo:inline>

				<xsl:text>: </xsl:text>
			</fo:block>

			<fo:list-block margin-left="{$textMarginLeft}"
			               font-size="{$standardFontSize}"
										 margin-top="{$mainMarginTop}">
				<xsl:for-each select="$values">
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

	<!-- table frequency -->	
	<xsl:template name = "table-frequency" >
		<xsl:param name = "process"/>

		<fo:block margin-top="{$mainMarginTop}">

			<fo:table table-layout="fixed"  
			          border-before-style="hidden" 
								border-after-style="hidden"
								border-start-style="hidden"
								border-end-style="hidden"> <!-- the style "none" is default, but still shows lines -->
                                           <!-- the width of the table cannot be controlled here, is done via the columns -->
				<!-- define the columns -->				
				<fo:table-column column-width="proportional-column-width(1)"/>	<!-- this construct is used to align the table in a centred way -->							
				<fo:table-column column-width="40%"/>                           
				<fo:table-column column-width="10%"/>
				<fo:table-column column-width="20%"/>
				<fo:table-column column-width="25%"/>
				<fo:table-column column-width="proportional-column-width(1)"/>  <!-- this is also part of central alignment approach --> 
				                                                                <!-- in addition the columns need to be numbered, omitting the first and last column --> 
				<!-- define the header -->
				<fo:table-header color="#808080">
					<fo:table-cell column-number="2" border-width="thin"  border-style="solid" border-start-style="hidden">
						<fo:block text-align="center" font-weight="bold">Art</fo:block>
					</fo:table-cell>
					<fo:table-cell column-number="3" border-width="thin" border-style="solid">
						<fo:block text-align="center" font-weight="bold">Dauer (h)</fo:block>
					</fo:table-cell>
					<fo:table-cell column-number="4" border-width="thin" border-style="solid">
						<fo:block text-align="center" font-weight="bold">Häufigkeit</fo:block>
					</fo:table-cell>
					<fo:table-cell column-number="5" border-width="thin" border-style="solid" border-end-style="hidden">
						<fo:block text-align="center" font-weight="bold">Zeitraum</fo:block>
					</fo:table-cell>										
				</fo:table-header>

				<fo:table-body>					
					<xsl:for-each select="$process">
						<fo:table-row>

							<fo:table-cell column-number="2" border-width="thin" border-style="solid" border-start-style="hidden">
							<fo:block text-align="center">
								<xsl:value-of select="current()"/>
							</fo:block>
						</fo:table-cell>

						<fo:table-cell column-number="3" border-width="thin" border-style="solid">
							<fo:block text-align="center">
								<xsl:value-of select="current()"/>
							</fo:block>
						</fo:table-cell>						

						<fo:table-cell column-number="4" border-width="thin" border-style="solid">
							<fo:block text-align="center">
								<xsl:value-of select="current()"/>
							</fo:block>
						</fo:table-cell>	

						<fo:table-cell column-number="5" border-width="thin" border-style="solid" border-end-style="hidden">
							<fo:block text-align="center">
								<xsl:value-of select="current()"/>
							</fo:block>
						</fo:table-cell>	

						</fo:table-row>
					</xsl:for-each>
				</fo:table-body>

			</fo:table>
		</fo:block>	
	</xsl:template>

</xsl:stylesheet>