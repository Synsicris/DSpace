<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1"
								xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
								xmlns:fo="http://www.w3.org/1999/XSL/Format"
								xmlns:cerif="https://www.openaire.eu/cerif-profile/1.1/"
								exclude-result-prefixes="fo">

	<!--#########################################################################-->
  <!-- VARIABLES -->	
	<!--#########################################################################-->

	<!-- paths -->	
	<!-- NOTE: path needs to be given with single hyphens otherwise the path is interpreted as XPath element --> 
	<xsl:param name="imageDirectory" select="'/opt/dspace/dspace-syn7/install/config/crosswalks/template'"/>

	<!-- font size -->	
	<xsl:param name="titleFontSize" select="'14pt'"/>
	<xsl:param name="mainFontSize" select="'10pt'"/> 

	<!-- margins -->	
	<xsl:param name="textMarginLeft" select="'3mm'"/>
	<xsl:param name="titleMarginTop" select="'5mm'"/>	
	<xsl:param name="mainMarginTop" select="'2mm'"/>
	<xsl:param name="sectionMarginTop" select="'10mm'"/>
	<xsl:param name="sectionMarginBottom" select="'-3mm'"/>		

	<!-- rulers -->
	<xsl:param name="titleRulerLength" select="'100%'"/>
	<xsl:param name="sectionRulerLength" select="'97.5%'"/>

	<!-- CURRENT PROBLEMS AND QUESTIONS -->	
	<!-- so far relative paths are not correctly handled, that is why absolute paths are given -->
	<!-- how to handle multi-language approaches? using variables? -->

	<!--#########################################################################-->
  <!-- MAIN PAGE -->	
	<!--#########################################################################-->

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
					<fo:region-body border-before-width="0"> <!-- according to the standard the width has be 0, 
																												this affectively avoids border lines to be implemented 
																												(unless the standard is ignored which DSpace does not do), 
																												as solution the other regions are used: 1pt and black background --> 
					</fo:region-body>	

					<!-- properties of header -->
					<fo:region-before extent="1pt"
														background-color="black">
					</fo:region-before>

					<!-- properties of footer -->
					<fo:region-after extent="1pt"
													 background-color="black">
					</fo:region-after>

					<!-- properties of the left side -->
					<fo:region-start extent="1pt"
													 background-color="black">
					</fo:region-start>

					<!-- properties of the right side -->
					<fo:region-end extent="1pt"
												 background-color="black">
					</fo:region-end>					

				</fo:simple-page-master>
			</fo:layout-master-set>

		<!-- PAGE SEQUENCE -->
			<fo:page-sequence master-reference="A4-process-events">
				<fo:flow flow-name="xsl-region-body">

					<!-- entity name -->
					<xsl:call-template name="entity-title-ruler">
				    <xsl:with-param name="entityName" select="'Ethik'" />
			    </xsl:call-template>
			    
					<!-- title -->
					<xsl:call-template name="key-value-single">
						<xsl:with-param name="label" select="'Titel'" />
						<xsl:with-param name="value" select="cerif:Title" />
					</xsl:call-template>

					<xsl:call-template name="key-value-comma-list">
				    <xsl:with-param name="label" select="'Type'" />
				    <xsl:with-param name="values" select="cerif:Aspects/cerif:Aspect/cerif:Type" />
			    </xsl:call-template>						

					<!-- table frequency of cooperation processes -->
					<xsl:call-template name="table-frequency">
						<xsl:with-param name="process" select="cerif:Aspects/cerif:Aspect/cerif:Type" />
					</xsl:call-template>

					<fo:block text-align="left" margin-top="5mm">
						<xsl:text>Where is the data?</xsl:text>
					</fo:block>					

					<xsl:for-each select="cerif:Aspects/cerif:Aspect">
						<fo:block font-size="10pt">
							<xsl:value-of select="cerif:Type" />
						</fo:block>
					</xsl:for-each>

					<!-- table frequency of cooperation processes two -->
					<xsl:call-template name="table-frequency-two">
						<xsl:with-param name="process" select="cerif:Aspects/cerif:Aspect" />
					</xsl:call-template>

				</fo:flow>
			</fo:page-sequence>

		</fo:root>
	</xsl:template>

	<!--#########################################################################-->
  <!-- USED TEMPLATES -->	
	<!--#########################################################################-->

	<!-- entity title with horizontal ruler -->	
	<xsl:template name = "entity-title-ruler" >
		<xsl:param name = "entityName" />

		<fo:block margin-left="{$textMarginLeft}"
							margin-top="{$titleMarginTop}">
			<fo:inline font-size="{$titleFontSize}"
								 font-weight="bold" 
								 text-align="left">							
				<xsl:value-of select="$entityName" />
			</fo:inline>
		</fo:block>

		<fo:block>
			<fo:leader leader-pattern="rule" 
			           leader-length="{$titleRulerLength}"
								 rule-style="solid" />         
		</fo:block>

	</xsl:template>

	<!-- section title with horizontal ruler -->	
	<xsl:template name = "section-title-ruler" >
		<xsl:param name = "sectionName" />

		<fo:block margin-left="{$textMarginLeft}"
							margin-top="{$sectionMarginTop}"
							margin-bottom="{$sectionMarginBottom}"
							font-size="{$mainFontSize}">
			<fo:inline text-align="left">							
				<xsl:value-of select="$sectionName" />
			</fo:inline>
		</fo:block>

		<fo:block text-align="center"> <!-- ruler cannot be aligned, this is done via the block -->
			<fo:leader leader-pattern="rule" 
			           leader-length="{$sectionRulerLength}" 
								 rule-style="solid" />
		</fo:block>

	</xsl:template>

	<!-- key: single value -->
	<xsl:template name = "key-value-single" >
	  <xsl:param name = "label" />
	  <xsl:param name = "value" />

	  <xsl:if test="$value">
		  <fo:block margin-left="{$textMarginLeft}"
								font-size="{$mainFontSize}" 
			        	margin-top="{$mainMarginTop}">

				<fo:inline font-weight="bold" text-align="right">
					<xsl:value-of select="$label" /> 
				</fo:inline>

				<xsl:text>: </xsl:text>

				<fo:inline>
					<xsl:value-of select="$value" /> 
				</fo:inline>

			</fo:block>
	  </xsl:if>
	</xsl:template>
	
	<!-- key: value list separated by commas -->
	<xsl:template name = "key-value-comma-list" >
		<xsl:param name = "label" />
	  <xsl:param name = "values" />

	  <xsl:if test="$values">
		  <fo:block margin-left="{$textMarginLeft}" 
								font-size="{$mainFontSize}" 
			          margin-top="{$mainMarginTop}">

				<fo:inline font-weight="bold" 
				           text-align="right"  >
					<xsl:value-of select="$label" /> 
				</fo:inline>

				<xsl:text>: </xsl:text>

				<fo:inline>
					<xsl:for-each select="$values">
					  <xsl:value-of select="current()" />
					  <xsl:if test="position() != last()">, </xsl:if> <!-- do only this for the last item -->
					</xsl:for-each>
				</fo:inline>

			</fo:block>
		</xsl:if>
	</xsl:template>	

	<!-- key: every value in a separated line -->
	<xsl:template name = "key-value-line-list" >
		<xsl:param name = "label" />
	  <xsl:param name = "values" />

		<xsl:if test="$values"> <!-- do only if there are values -->

			<fo:block margin-left="{$textMarginLeft}"
								font-size="{$mainFontSize}" 
			          margin-top="{$mainMarginTop}">
				<fo:inline font-weight="bold" 
				           text-align="right"  >
					<xsl:value-of select="$label" /> 
				</fo:inline>

				<xsl:text>: </xsl:text>
			</fo:block>

			<fo:list-block font-size="{$mainFontSize}">
				<xsl:for-each select="$values">
					<fo:list-item>

						<fo:list-item-label>      <!-- for whatever reason there is no space between label and body --> 
							<fo:block></fo:block>   <!-- so the label is skipped here and moved to the body -->  
						</fo:list-item-label>
				
						<fo:list-item-body>
							<fo:block>- <xsl:value-of select="current()" /></fo:block>
						</fo:list-item-body>

				  </fo:list-item>

			  </xsl:for-each>
		  </fo:list-block>
	  </xsl:if>

  </xsl:template>	

	<!-- table frequency -->	
	<xsl:template name = "table-frequency" >
		<xsl:param name = "process" />

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
								<xsl:value-of select="current()" />
							</fo:block>
						</fo:table-cell>

						<fo:table-cell column-number="3" border-width="thin" border-style="solid">
							<fo:block text-align="center">
								<xsl:value-of select="current()" />
							</fo:block>
						</fo:table-cell>						

						<fo:table-cell column-number="4" border-width="thin" border-style="solid">
							<fo:block text-align="center">
								<xsl:value-of select="current()" />
							</fo:block>
						</fo:table-cell>	

						<fo:table-cell column-number="5" border-width="thin" border-style="solid" border-end-style="hidden">
							<fo:block text-align="center">
								<xsl:value-of select="current()" />
							</fo:block>
						</fo:table-cell>	

						</fo:table-row>
					</xsl:for-each>
				</fo:table-body>

			</fo:table>
		</fo:block>	
	</xsl:template>	

	
	<!-- table frequency -->	
	<xsl:template name = "table-frequency-two" >
		<xsl:param name = "process" />

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
				<fo:table-column column-width="40%"/>
				<fo:table-column column-width="proportional-column-width(1)"/>  <!-- this is also part of central alignment approach --> 
				                                                                <!-- in addition the columns need to be numbered, omitting the first and last column --> 
				<!-- define the header -->
				<fo:table-header color="#808080">
					<fo:table-cell column-number="2" border-width="thin"  border-style="solid" border-start-style="hidden">
						<fo:block text-align="center" font-weight="bold">Art</fo:block>
					</fo:table-cell>
					<fo:table-cell column-number="3" border-width="thin" border-style="solid" border-end-style="hidden">
						<fo:block text-align="center" font-weight="bold">Dauer (h)</fo:block>
					</fo:table-cell>										
				</fo:table-header>

				<fo:table-body>					
					<xsl:for-each select="$process">
						<fo:table-row>

							<fo:table-cell column-number="2" border-width="thin" border-style="solid" border-start-style="hidden">
							<fo:block text-align="center">
								<xsl:value-of select="cerif:Type" />
							</fo:block>
						</fo:table-cell>

						<fo:table-cell column-number="3" border-width="thin" border-style="solid" border-end-style="hidden">
							<fo:block text-align="center">
								<xsl:value-of select="cerif:Type" />
							</fo:block>
						</fo:table-cell>						

						</fo:table-row>
					</xsl:for-each>
				</fo:table-body>

			</fo:table>
		</fo:block>	
	</xsl:template>

</xsl:stylesheet>