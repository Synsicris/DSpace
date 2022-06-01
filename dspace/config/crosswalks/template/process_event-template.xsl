<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1"
								xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
								xmlns:fo="http://www.w3.org/1999/XSL/Format"
								xmlns:cerif="https://www.openaire.eu/cerif-profile/1.1/"
								exclude-result-prefixes="fo">
	
	<!-- path needs to be given with single hyphens otherwise the path is interpreted 
		   as XPath element --> 
	<xsl:param name="imageDirectory" select="'/opt/dspace/dspace-syn7/install/config/crosswalks/template'"/>

	<!--#########################################################################-->
  <!-- MAIN PAGE -->	
	<!--#########################################################################-->

	<xsl:template match="cerif:Project">	
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

		<!-- LAYOUT MASTER SET-->
			<fo:layout-master-set>
				<fo:simple-page-master master-name="simpleA4"
															 page-height="29.7cm" 
															 page-width="24cm" 
															 margin-top="2cm"
															 margin-bottom="2cm" 
															 margin-left="1cm" 
															 margin-right="1cm">
					<fo:region-body />
				</fo:simple-page-master>
			</fo:layout-master-set>

		<!-- PAGE SEQUENCE -->
			<fo:page-sequence master-reference="simpleA4">
				<fo:flow flow-name="xsl-region-body">

				  <!-- title -->
		      <fo:block margin-bottom="5mm" 
					          padding="2mm">
						<fo:block font-size="26pt" 
						          font-weight="bold" 
											text-align="center" >
							<xsl:value-of select="cerif:Title" />
						</fo:block>
					</fo:block>

					<!-- basic information -->
					<xsl:call-template name="section-title">
				    <xsl:with-param name="label" select="'Basic informations'" />
			    </xsl:call-template>
			    
					<!-- description -->
					<xsl:call-template name="print-value">
				    <xsl:with-param name="label" select="'Description'" />
				    <xsl:with-param name="value" select="cerif:Description" />
			    </xsl:call-template>
			   
					<!-- purposes (comma list) -->
					<xsl:call-template name="print-values-comma-list">
				    <xsl:with-param name="label" select="'Purpose'" />
				    <xsl:with-param name="values" select="cerif:Purpose" />
			    </xsl:call-template>
			    
					<!-- involved organisations (comma list) -->
					<xsl:call-template name="print-values-comma-list">
				    <xsl:with-param name="label" select="'Involved organisations'" />
				    <xsl:with-param name="values" select="cerif:Organisation" />
			    </xsl:call-template>
			    
					<!-- involved stakeholders (comma list) -->
					<xsl:call-template name="print-values-line-list">
				    <xsl:with-param name="label" select="'Involved stakeholders'" />
				    <xsl:with-param name="values" select="cerif:Stakeholder" />
			    </xsl:call-template>

					<!-- add icon -->
					<xsl:call-template name="information-icon">
						<xsl:with-param name="values" select="cerif:Stakeholder" />
					</xsl:call-template>

					<!-- add table -->
					<xsl:call-template name="test-table">
						<xsl:with-param name="values" select="cerif:Stakeholder" />
					</xsl:call-template>

				</fo:flow>
			</fo:page-sequence>

		</fo:root>
	</xsl:template>

	<!--#########################################################################-->
  <!-- USED TEMPLATES -->	
	<!--#########################################################################-->

	<!-- key: value -->
	<xsl:template name = "print-value" >
	  <xsl:param name = "label" />
	  <xsl:param name = "value" />

	  <xsl:if test="$value">
		  <fo:block font-size="10pt" 
			        	margin-top="2mm">

				<fo:inline font-weight="bold" text-align="right" >
					<xsl:value-of select="$label" /> 
				</fo:inline >

				<xsl:text>: </xsl:text>

				<fo:inline>
					<xsl:value-of select="$value" /> 
				</fo:inline >

			</fo:block>
	  </xsl:if>
	</xsl:template>
	
	<!-- key: value list separated by commas -->
	<xsl:template name = "print-values-comma-list" >
		<xsl:param name = "label" />
	  <xsl:param name = "values" />

	  <xsl:if test="$values">
		  <fo:block font-size="10pt" margin-top="2mm">

				<fo:inline font-weight="bold" text-align="right"  >
					<xsl:value-of select="$label" /> 
				</fo:inline >

				<xsl:text>: </xsl:text>

				<fo:inline>
					<xsl:for-each select="$values">
					  <xsl:value-of select="current()" />
					  <xsl:if test="position() != last()">, </xsl:if> <!-- do only this for the last item -->
					</xsl:for-each>
				</fo:inline >

			</fo:block>
		</xsl:if>
	</xsl:template>	

	<!-- key: every value in a separated line -->
	<xsl:template name = "print-values-line-list" >
		<xsl:param name = "label" />
	  <xsl:param name = "values" />

		<xsl:if test="$values"> <!-- do only if there are values -->

			<fo:block font-size="10pt" 
			          margin-top="2mm">
				<fo:inline font-weight="bold" 
				           text-align="right"  >
					<xsl:value-of select="$label" /> 
				</fo:inline >

				<xsl:text>: </xsl:text>
			</fo:block>

			<fo:list-block font-size="10pt">
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

	<!-- information icon -->	
	<xsl:template name = "information-icon" >
		<xsl:param name = "values" />
		<xsl:variable name="imageFile" select="concat('file:',$imageDirectory,'/','information-icon.png')" />

		<fo:block text-align="left"
							margin-top="10mm">
			<xsl:text>Image file: </xsl:text>
			<fo:inline font-weight="bold" text-align="right"  >
				<xsl:value-of select="$imageFile" /> 
			</fo:inline >
		</fo:block>

		<fo:block text-align="left">
			<xsl:text>Counting (multiplied): </xsl:text>
			<fo:inline font-weight="bold" text-align="right"  >
				<xsl:value-of select="count($values)*3" /> 
			</fo:inline >
		</fo:block>

		<fo:block text-align="left" 
		          font-size="10pt" 
							margin-top="2mm">
			<xsl:text>This is an icon</xsl:text>

			<fo:external-graphic content-height="20" 
													 content-width="20"
												 	 scaling="uniform">

				<xsl:attribute name="src">
					<xsl:value-of select="$imageFile" />
				</xsl:attribute>														

			</fo:external-graphic>

		</fo:block>	
  </xsl:template>	

	<!-- table -->	
	<xsl:template name = "test-table" >
		<xsl:param name = "values" />

		<fo:block text-align="left"
							margin-top="10mm">
			<xsl:text>This is a table: </xsl:text>
		</fo:block>

		<fo:block>
			<fo:table table-layout="fixed" width="100%" border-style="solid" border-width="thick">
				<fo:table-column column-width="20%"/>
				<fo:table-column column-width="80%"/>

				<fo:table-header background-color="#1a8cff" color="#ffffff">
					<fo:table-cell border-width="thin" border-style="solid">
						<fo:block text-align="center" font-weight="bold">ID</fo:block>
					</fo:table-cell>
					<fo:table-cell border-width="thin" border-style="solid">
						<fo:block text-align="center" font-weight="bold">Value</fo:block>
					</fo:table-cell>
				</fo:table-header>

				<fo:table-body>					
					<fo:table-row>
						<fo:table-cell border-width="thin" border-style="solid">
							<fo:block text-align="center">1</fo:block>
						</fo:table-cell>

						<fo:table-cell border-width="thin" border-style="solid">
							<fo:block text-align="center">ABC</fo:block>
						</fo:table-cell>
					</fo:table-row>

					<xsl:for-each select="$values">
						<fo:table-row>

							<fo:table-cell border-width="thin" border-style="solid">
								<fo:block text-align="center">
									<xsl:number value="position()+1" format="1" /> <!-- counting via xsl:number--> 
								</fo:block>
							</fo:table-cell>
	
							<fo:table-cell border-width="thin" border-style="solid">
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

	<!-- section title with horizontal ruler -->	
	<xsl:template name = "section-title" >
		<xsl:param name = "label" />

		<fo:block font-size="16pt" font-weight="bold" margin-top="8mm" >
			<xsl:value-of select="$label" /> 
		</fo:block>

		<fo:block>
			<fo:leader leader-pattern="rule" leader-length="100%" rule-style="solid" />         
		</fo:block>

	</xsl:template>
	
</xsl:stylesheet>