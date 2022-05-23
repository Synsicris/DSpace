<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:cerif="https://www.openaire.eu/cerif-profile/1.1/"
	exclude-result-prefixes="fo">
	
	<xsl:param name="imageDir" />
	
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

		<!-- START PAGE -->

			<fo:page-sequence master-reference="simpleA4">
				<fo:flow flow-name="xsl-region-body">

				  <!-- title -->
		      <fo:block margin-bottom="5mm" padding="2mm">
						<fo:block font-size="26pt" font-weight="bold" text-align="center" >
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
				    
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>

	<!-- PRINT STYLES -->
	<!-- key: value -->
	<xsl:template name = "print-value" >
	  <xsl:param name = "label" />
	  <xsl:param name = "value" />

	  <xsl:if test="$value">
		  <fo:block font-size="10pt" margin-top="2mm">

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

			<fo:block font-size="10pt" margin-top="2mm">
				<fo:inline font-weight="bold" text-align="right"  >
					<xsl:value-of select="$label" /> 
				</fo:inline >

				<xsl:text>: </xsl:text>
			</fo:block>

			<fo:list-block>
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