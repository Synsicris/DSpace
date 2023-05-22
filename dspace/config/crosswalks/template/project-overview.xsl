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
<xsl:param name="lang.title.section1.1.1" select="'1.1.1 Beschreibung des Gesamtziels des Vorhabens über den Impact Pathway'"/>
<xsl:param name="lang.title.section1.2" select="'1.2 Wissenschaftliche und/oder technische Arbeitsziele des Vorhabens'"/>
<xsl:param name="lang.title.section1.3" select="'1.3 Arbeitsziele zur Interaktion'"/>
<xsl:param name="lang.title.section1.4" select="'1.4 Bezug des Vorhabens zu den förderpolitischen Zielen'"/>

<xsl:param name="lang.title.section4" select="'4 Verwertung und Wirkung'"/>
<xsl:param name="lang.title.section4.1" select="'4.1 Überblick'"/>
<xsl:param name="lang.title.section4.2" select="'4.2 Anwendung(smöglichkeiten)'"/>
<xsl:param name="lang.title.section4.2.1" select="'4.2.1 Lösung/Veränderung/Innovation'"/>
<xsl:param name="lang.title.section4.2.2" select="'4.2.2 Ideen für eine Lösung/Veränderung/Innovation'"/>
<xsl:param name="lang.title.section4.2.3" select="'4.2.3 Anwendung'"/>
<xsl:param name="lang.title.section4.2.4" select="'4.2.4 Unerwartete Erkentnisse und Probleme'"/>
<xsl:param name="lang.title.section4.2.5" select="'4.2.5 Patente'"/>
<xsl:param name="lang.title.section4.2.6" select="'4.2.6 Ausgründungen'"/>
<xsl:param name="lang.title.section4.2.7" select="'4.2.7 Arbeiten und Kooperationen nach Projektende'"/>
<xsl:param name="lang.title.section4.2.8" select="'4.2.8 Auszeichnungen und Preise'"/>
<xsl:param name="lang.title.section4.2.9" select="'4.2.9 Offene Forschungsfragen'"/>

<xsl:param name="lang.title.section4.3" select="'4.3 Gesellschaftliche Wirkungen und Reflexionen zum Projekt'"/>
<xsl:param name="lang.title.section4.3.1" select="'4.3.1 Rahmenbedingungen'"/>
<xsl:param name="lang.title.section4.3.2" select="'4.3.2 Gesellschaftliche Wirkung'"/>
<xsl:param name="lang.title.section4.3.3" select="'4.3.3 Mögliche negative Nebenwirkungen'"/>
<xsl:param name="lang.title.section4.3.4" select="'4.3.4 Ethische Aspekte'"/>
<xsl:param name="lang.title.section4.3.5" select="'4.3.5 Gender'"/>

<!-- TODO: add all sections -->

<!-- individual fields -->
<xsl:param name="lang.title" select="'Titel'"/>
<xsl:param name="lang.description" select="'Beschreibung'"/>
<xsl:param name="lang.date" select="'Date'"/>
<xsl:param name="lang.abstract" select="'Zusammenfassung'"/>
<xsl:param name="lang.language" select="'Sprache'"/>
<xsl:param name="lang.term" select="'Laufzeit'"/>
<xsl:param name="lang.duration.month" select="'Dauer in Monaten'"/>
<xsl:param name="lang.postcode-city-country" select="'PLZ / Stadt / Land'"/>
<xsl:param name="lang.website" select="'Webadresse'"/>
<xsl:param name="lang.currency" select="'Währung'"/>

<xsl:param name="lang.organisation.form" select="'Organisationsform'"/>
<xsl:param name="lang.cooperation.type" select="'Art der Kooperation'"/>

<xsl:param name="lang.funding.call" select="'Förderbereich/Bekanntmachung'"/>
<xsl:param name="lang.funding.programme" select="'Fördermaßnahme/Förderprogramm'"/>

<xsl:param name="lang.projectpartner.easy-online" select="'Ausführende Stelle im easy-online-Antrag'"/>
<xsl:param name="lang.projectpartner.field-of-activity" select="'Tätigkeitsbereich der Organisationseinheit'"/>
<xsl:param name="lang.projectpartner.destatis" select="'Disziplin / Fachgebiet der Forschungseinrichtung'"/>
<xsl:param name="lang.projectpartner.nace" select="'Wirtschaftszweig der Organisation'"/>
<xsl:param name="lang.projectpartner.political-level" select="'Politikebene(n) der Organisationseinheit'"/>

<xsl:param name="lang.organisation.field-of-activity" select="'Tätigkeitsbereich der Organisation'"/>
<xsl:param name="lang.organisation.nace" select="'Wirtschaftszweig der Organisation'"/>
<xsl:param name="lang.organisation.destatis" select="'Disziplin / Fachgebiet der Organisation'"/>
<xsl:param name="lang.organisation.political-level" select="'Politikebenen der Organisation'"/>
<xsl:param name="lang.organisation.project-contribution" select="'Beschreibung der Zusammenarbeit'"/>			
<xsl:param name="lang.organisation.size" select="'Unternehmensgröße'"/>
<xsl:param name="lang.organisation.location-type" select="'Verortung der Akteursgruppe'"/>			

<xsl:param name="lang.targetgroup.relevance" select="'Relevanz der Akteursgruppe für das Vorhaben'"/>			

<xsl:param name="lang.workpackage.description" select="'Beschreibung'"/>
<xsl:param name="lang.workpackage.requirement" select="'Voraussetzungen'"/>
<xsl:param name="lang.workpackage.responsible-organisation" select="'Hauptverantwortlicher Projektpartner'"/>

<xsl:param name="lang.innovationpotential.type" select="'Art'"/>
<xsl:param name="lang.innovationpotential.targetgroup" select="'Nutzende Akteursgruppe(n)'"/>
<xsl:param name="lang.innovationpotential.description" select="'Beschreibung (Nutzen, Auswirkung)'"/>
<xsl:param name="lang.innovationpotential.applicability-novelty" select="'Innovationsgrad'"/>
<xsl:param name="lang.innovationpotential.applicability-efficiency" select="'Effizienz'"/>
<xsl:param name="lang.innovationpotential.applicability-practicability" select="'Praktikabilität und Anschlussfähigkeit'"/>
<xsl:param name="lang.innovationpotential.applicability-requirements" select="'Vorraussetzungen'"/>
<xsl:param name="lang.innovationpotential.applicability-prospects" select="'Aussichten für eine Etablierung / Verbreitung / Übertragung'"/>
<xsl:param name="lang.innovationpotential.regionalscope-primaryfield" select="'Primärer Einsatzbereich'"/>
<xsl:param name="lang.innovationpotential.regionalscope-laterdissimination" select="'spätere Verbreitung'"/>
<xsl:param name="lang.innovationpotential.regionalscope-description" select="'Erläuterung'"/>
<xsl:param name="lang.innovationpotential.srltrl-developmentprojectstart" select="'Entwicklungsstand bei Projektbeginn'"/>
<xsl:param name="lang.innovationpotential.srltrl-developmentprojectend" select="'Entwicklungsstand bei Projektende'"/>
<xsl:param name="lang.innovationpotential.srltrl-selection" select="'TRL/SRL?'"/>
<xsl:param name="lang.innovationpotential.srltrl-readinessprojectstart" select="'Reifegrad bei Projektbeginn'"/>
<xsl:param name="lang.innovationpotential.srltrl-readinessprojectend" select="'Reifegrad bei Projektende'"/>

<xsl:param name="lang.innovationidea.type" select="'Art'"/>
<xsl:param name="lang.innovationidea.description" select="'Bezeichnung'"/>

<xsl:param name="lang.application.related-innovation" select="'Verlinkung zur dokumentierten Lösung / Veränderung / Innovation oder einem Patent'"/>
<xsl:param name="lang.application.unit" select="'Einheit'"/>
<xsl:param name="lang.application.free-unit" select="'Einheit (eigene Angabe)'"/>
<xsl:param name="lang.application.reference-year" select="'Referenzjahr'"/>
<xsl:param name="lang.application.quantity" select="'Menge / Anzahl (Steigerung)'"/>
<xsl:param name="lang.application.regionaloutreach" select="'Region der Anwendung'"/>
<xsl:param name="lang.application.success-description" select="'Anmerkungen / Weblink'"/>

<xsl:param name="lang.unexpectedresult.resultdescription" select="'Berschreibung Ergebniss / Problem'"/>
<xsl:param name="lang.unexpectedresult.statuspublication" select="'Veröffentlichung geplant / erfolgt'"/>
<xsl:param name="lang.unexpectedresult.informationpublication" select="'Information zur Veröffentlichung'"/>
<xsl:param name="lang.unexpectedresult.linkpublication" select="'Titel der Veröffentlichung'"/>

<xsl:param name="lang.patent.type" select="'Art'"/>
<xsl:param name="lang.patent.use" select="'Form der geplanten Nutzung'"/>	
<xsl:param name="lang.patent.use-description" select="'Erläuterung zur Nutzung'"/>			
<xsl:param name="lang.patent.registration-number" select="'Anmeldenummer'"/>
<xsl:param name="lang.patent.registration-date" select="'Datum der Anmeldung'"/>
<xsl:param name="lang.patent.holder" select="'Inhaber:in'"/>	
<xsl:param name="lang.patent.patent-number" select="'Patentnummer'"/>
<xsl:param name="lang.patent.approval-date" select="'Datum der Zulassung'"/>
<xsl:param name="lang.patent.publication-date" select="'Datum der Veröffentlichung'"/>
<xsl:param name="lang.patent.inventor" select="'Erfinder:in(nen)'"/>
<xsl:param name="lang.patent.issuer" select="'Erteiler'"/>
<xsl:param name="lang.patent.ipc-class" select="'IPC-Klasse(n)'"/>

<xsl:param name="lang.spinoff.description" select="'Beschreibung'"/>
<xsl:param name="lang.spinoff.result-registercourt" select="'Ort des zuständigen Gerichts'"/>
<xsl:param name="lang.spinoff.result-registertype" select="'Registerart'"/>
<xsl:param name="lang.spinoff.result-registernumber" select="'Registernummer'"/>
<xsl:param name="lang.spinoff.result-foundingdate" select="'Datum der Eintragung'"/>
<xsl:param name="lang.spinoff.result-type" select="'Art der Ausgründung'"/>
<xsl:param name="lang.spinoff.result-nace" select="'Wirtschaftszweig'"/>
<xsl:param name="lang.spinoff.result-street" select="'Straße, Hausnummer'"/>
<xsl:param name="lang.spinoff.result-postcode" select="'PLZ'"/>
<xsl:param name="lang.spinoff.result-city" select="'Ort'"/>
<xsl:param name="lang.spinoff.result-country" select="'Land'"/>
<xsl:param name="lang.spinoff.result-webaddress" select="'Homepage der Ausgründung'"/>
<xsl:param name="lang.spinoff.result-relatedorganisation" select="'Ausgründende Organisation(en)'"/>
<xsl:param name="lang.spinoff.funding-sectiontitle" select="'Unterstützungsleistung'"/>
<xsl:param name="lang.spinoff.funding-year" select="'Jahr'"/>
<xsl:param name="lang.spinoff.funding-title" select="'Förderprogramm'"/>
<xsl:param name="lang.spinoff.funding-amount" select="'Fördersumme'"/>
<xsl:param name="lang.spinoff.funding-description" select="'Nicht finanzielle Unterstützung'"/>
<xsl:param name="lang.spinoff.performance-title" select="'Wirtschaftliche Entwicklung'"/>
<xsl:param name="lang.spinoff.performance-year" select="'Jahr'"/>
<xsl:param name="lang.spinoff.performance-amount" select="'Jahresumsatz'"/>

<xsl:param name="lang.step.partner" select="'Beteiligte Projektpartner/Unterauftragsnehmer/Kooperationspartner'"/>
<xsl:param name="lang.step.targetgroup" select="'Beteiligte Akteursgruppen'"/>
<xsl:param name="lang.step.type" select="'Art der Arbeit'"/>
<xsl:param name="lang.step.date" select="'Zeithorizont für die Realisierung'"/>
<xsl:param name="lang.step.description" select="'Beschreibung'"/>

<xsl:param name="lang.award.description" select="'Beschreibung'"/>
<xsl:param name="lang.award.date" select="'Datum der Verleihung'"/>
<xsl:param name="lang.award.pricemoney" select="'Preisgeld'"/>
<xsl:param name="lang.award.winner" select= "'Ausgezeichnete Person(en)'"/>
<xsl:param name="lang.award.organisation" select= "'Ausgezeichnete Organisation(en)'"/>
<xsl:param name="lang.award.donor" select= "'Initiator(en)'"/>
<xsl:param name="lang.award.targetgroup" select= "'Akteursgruppe des Initiators'"/>

<xsl:param name="lang.openresearchquestion.description" select= "'Beschreibung'"/>

<xsl:param name="lang.condition.conditiontype" select="'Art der Rahmenbedingung'"/>
<xsl:param name="lang.condition.influence" select="'Einfluss auf die Wirkung des Projektes'"/>
<xsl:param name="lang.condition.startframework" select="'Startzeitpunkt der Wirksamkeit'"/>
<xsl:param name="lang.condition.conditiondescription" select="'Beschreibung'"/>

<xsl:param name="lang.expectedsocietalimpact.description" select="'Beschreibung'"/>
<xsl:param name="lang.expectedsocietalimpact.transitionarea" select="'Art der Rahmenbedingung'"/>
<xsl:param name="lang.expectedsocietalimpact.evidence" select="'Einfluss auf die Wirkung des Projektes'"/>
<xsl:param name="lang.expectedsocietalimpact.type" select="'Art des Einflusses'"/>
<xsl:param name="lang.expectedsocietalimpact.type-coreelement" select="'Hauptelement'"/>
<xsl:param name="lang.expectedsocietalimpact.type-intensity" select="'Wirkungsintensität'"/>
<xsl:param name="lang.expectedsocietalimpact.type-description" select="'Umfang der erwarteten Wirkung'"/>

<xsl:param name="lang.negativesideeffect.description" select="'Beschreibung'"/>
<xsl:param name="lang.negativesideeffect.transitionArea" select="'Adressierter Transformationsbereich'"/>
<xsl:param name="lang.negativesideeffect.evidence" select="'Fachliche Grundlage'"/>
<xsl:param name="lang.negativesideeffect.type" select="'Art des Einflusses'"/>
<xsl:param name="lang.negativesideeffect.type-coreelement" select="'Kernelement der Nachhaltigkeit'"/>
<xsl:param name="lang.negativesideeffect.type-intensity" select="'Intensität der negativen Nebenwirkung'"/>
<xsl:param name="lang.negativesideeffect.type-description" select="'Umfang der negativen Wirkung'"/>

<xsl:param name="lang.ethic.keymessage" select="'Ethische Reflexion'"/>
<xsl:param name="lang.ethic.furtherdocuments" select="'Weitergehende Dokumente verfügbar'"/>
<xsl:param name="lang.ethic.reflection-aspecttype" select="'Relevanter ethischer Aspekt'"/>
<xsl:param name="lang.ethic.reflection-ethicdescription" select="'Vorgehen im Vorhaben für einen angemessenen Umgang'"/>

<xsl:param name="lang.gender.reflection-researchquestion" select="'Gibt es einen Geschlechterbezug in der Problemstellung?'"/>
<xsl:param name="lang.gender.reflection-descriptionresearchquestion" select="'Erläuterung zur Problemstellung'"/>
<xsl:param name="lang.gender.reflection-stateofresearch" select="'Gibt es einen Geschlechterbezug im Forschungsstand?'"/>
<xsl:param name="lang.gender.reflection-descriptionstateofresearch" select="'Erläuterung zum Foschungsstand'"/>
<xsl:param name="lang.gender.reflection-relevance" select="'Berücksichtigung von Geschlechteraspekten im Projektdesign'"/>
<xsl:param name="lang.gender.reflection-descriptionrelevance" select="'Erläuterung zu Geschlechteraspekten im Projektdesign'"/>
<xsl:param name="lang.gender.reflection-application" select="'Berücksichtigung geschlechterspezifischer Unterschiede hinsichtlich der Anwendung und Wirkung der Projektergebnisse'"/>
<xsl:param name="lang.gender.reflection-descriptionapplication" select="'Erläuterung zu geschlechtsspezifischen Unterschiede hinsichtlich der Anwendung und Wirkung'"/>
<xsl:param name="lang.gender.reflection-implementation" select="'Umsetzung direkter Maßnahmen zur Förderung der Gleichstellung bei den Beschäftigten im Projekt'"/>
<xsl:param name="lang.gender.reflection-descriptionimplementaiton" select="'Erläuterung zu Maßnahmen zur Förderung der Gleichstellung im Projekt'"/>
<xsl:param name="lang.gender.reflection-genderbalance" select="'Geschlechterverhältnis bei den Beschäftigten im Projekt'"/>
<xsl:param name="lang.gender.reflection-descriptiongenderbalance" select="'Erläuterung zum Geschlechterverhältnis'"/>

<xsl:param name="lang.overview.event" select="'Lösung/Veränderung/Innovation'"/>
<xsl:param name="lang.overview.process-event" select="'Kooperations-prozesse'"/>
<xsl:param name="lang.overview.physical-object" select="'Objekte/Materialien'"/>
<xsl:param name="lang.overview.planned-publication" select="'Geplante Veröffentlichungen'"/>
<xsl:param name="lang.overview.publication" select="'Veröffentlichungen'"/>
<xsl:param name="lang.overview.product" select="'Forschungsdaten'"/>
<xsl:param name="lang.overview.innovation-potential" select="'Events'"/>
<xsl:param name="lang.overview.innovation-idea" select="'Ideen für eine Lösung/Veränderung/Innovation'"/>
<xsl:param name="lang.overview.application" select="'Anwendung'"/>
<xsl:param name="lang.overview.unexpected-result" select="'Unerwartete Erkentnisse und Probleme'"/>
<xsl:param name="lang.overview.patent" select="'Patente'"/>
<xsl:param name="lang.overview.spinoff" select="'Aus-gründungen'"/>
<xsl:param name="lang.overview.further-steps" select="'Arbeiten und Kooperationen nach Projektende'"/>
<xsl:param name="lang.overview.award" select="'Aus-zeichnungen und Preise'"/>
<xsl:param name="lang.overview.open-research-question" select="'Offene Forschungs-fragen'"/>

<!-- TODO: add all fields -->

<!--########################################################################-->
<!-- VARIABLES -->	
<!--########################################################################-->

<!-- paths -->
<!-- INFO: so far relative paths are not correctly handled, that is why absolute paths are given -->	
<xsl:param name="imageDirectory" select="'/opt/dspace/dspace-syn7/install/config/crosswalks/template/images/'"/>

<!-- INFO: this input is necessary for the screenshots -->	
<xsl:param name="imageDir" />

<!-- font sizes -->	
<xsl:param name="font.size.title" select="'24pt'"/>
<xsl:param name="font.size.section-title" select="'14pt'"/>
<xsl:param name="font.size.sub-section-title" select="'12pt'"/>
<xsl:param name="font.size.sub-sub-section-title" select="'11pt'"/>
<xsl:param name="font.size.standard" select="'10pt'"/>
<xsl:param name="font.size.key" select="'11pt'"/>
<xsl:param name="font.size.key.alternative" select="'8pt'"/> 

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
<xsl:param name="padding.before.border" select="'1.5mm'"/>
<xsl:param name="padding.after.border" select="'4mm'"/>
<xsl:param name="padding.start.border" select="'3mm'"/>
<xsl:param name="padding.end.border" select="'3mm'"/>

<!-- lengths -->
<xsl:param name="length.ruler" select="'100%'"/>

<!-- widths -->
<xsl:param name="width.border" select="'0.3mm'"/>
<xsl:param name="width.table" select="'97%'"/>
<xsl:param name="width.ruler" select="'100%'"/>
<xsl:param name="width.icon.overview" select="'12mm'"/>

<!-- colours -->
<xsl:param name="colour.border" select="'#808080'"/>
<xsl:param name="colour.key" select="'#FF0000'"/>

<!-- styles -->
<xsl:param name="style.border" select="'solid'"/>
<xsl:param name="style.ruler" select="'solid'"/>

<!-- TODO: check number of entries on all tables -->

<!--########################################################################-->
<!-- MAIN PAGE -->	
<!--########################################################################-->

	<xsl:template match="cerif:Project">	
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

    <!--==========================================================--> 
		<!-- LAYOUT MASTER SET-->
    <!--==========================================================--> 

			<fo:layout-master-set>

			<!-- portrait page master -->
				<fo:simple-page-master master-name="A4-portrait"
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

			<!-- landscape page master -->
				<fo:simple-page-master master-name="A4-landscape"
															 page-height="24cm" 
															 page-width="29.7cm" 
															 margin-top="1cm"
															 margin-bottom="1cm" 
															 margin-left="2cm" 
															 margin-right="2cm">

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

			<fo:page-sequence master-reference="A4-portrait"
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
					<!-- SECTION #-1 - TESTING ON THE FIRST PAGE --> 
          <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->

					<!-- new page -->
          <fo:block break-after='page'/>

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
					<xsl:call-template name="project-partner-part1"/>

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

					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section1.1.1"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
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

				  <!-- sub section title - relation of the project to the objectives of the funding policy -->
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

          <!-- TODO: add template FIXME: unclear which entity to use-->
					<xsl:call-template name="research-work"/>

				  <!-- sub sub section title - interaction and transfer -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="'3.1.4	Arbeiten zur Interaktion und Transfer'"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

          <xsl:call-template name="interaction-transfer-overview"/>

          <!-- TODO: here all entities belonging to transfer need to be added -->

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

					<!-- INFO: this section we don't need to fill -->

          <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
					<!-- SECTION #4 --> 
          <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
 
					<!-- new page -->
          <fo:block break-after='page'/>

          <!-- section title - exploitation and impact -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section4"/>
						<xsl:with-param name="fontSize" select="$font.size.section-title"/>
					</xsl:call-template>

				  <!-- sub section title - overview -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section4.1"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-section-title"/>
					</xsl:call-template>

          <xsl:call-template name="exploitation-effect-overview"/>

				  <!-- sub section title - application possibilities -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section4.2"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-section-title"/>
					</xsl:call-template>

				  <!-- sub sub section title - solutions/changes/innovations -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section4.2.1"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

          <!-- TODO: add template - unclear which entity to consider-->
					<xsl:call-template name="innovation-potential"/>

					<!-- sub sub section title - ideas for solutions/changes/innovations -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section4.2.2"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

					<xsl:call-template name="innovation-idea"/>

					<!-- sub sub section title - application -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section4.2.3"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

          <!-- FIXME: using this template results in an empty (15 byte), unclear why since some projects have entries, others not -->

					<xsl:call-template name="application"/>

					<!-- sub sub section title - unexpected results -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section4.2.4"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

          <xsl:call-template name="unexpected-result"/>

					<!-- sub sub section title - patents -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section4.2.5"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

          <xsl:call-template name="patent"/>

					<!-- sub sub section title - spinoffs -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section4.2.6"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

          <!-- TODO: check if this template causes problems too -->
        	<xsl:call-template name="spinoff"/>

					<!-- sub sub section title - work and cooperation after the project -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section4.2.7"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>        
	
					<xsl:call-template name="furthersteps"/>

					<!-- sub sub section title - awards -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section4.2.8"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

					<xsl:call-template name="award"/>

					<!-- sub sub section title - open research questions -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section4.2.9"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

          <!-- TODO: check if this template causes problems too -->
					<xsl:call-template name="open-research-question"/>

				  <!-- sub section title - social impact and reflections of the project -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section4.3"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-section-title"/>
					</xsl:call-template>

				  <!-- sub sub section title - condition -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section4.3.1"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

					<xsl:call-template name="condition"/>

				  <!-- sub sub section title - expected societal impact -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section4.3.2"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

          <!-- FIXME: using this template results in an empty (15 byte) report IF NO data are given -->
 					<xsl:call-template name="expected-societal-impact"/>

				  <!-- sub sub section title - description of potential negative side effects -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section4.3.3"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

					<!-- FIXME: using this template results in an empty (15 byte), unclear why since all projects have entries -->
					<!-- working again for the reporting project after i deleted an empty entry (just with title), when i created another entry only with title and description, the report was broken again  -->
				 <xsl:call-template name="negative-side-effect"/> 

				  <!-- sub sub section title - description of ethical aspects -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section4.3.4"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

          <!-- FIXME: using this template results in an empty (15 byte), unclear why since some projects have entries, others not -->
					 <xsl:call-template name="ethics"/>

				  <!-- sub sub section title - description of gender aspects -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section4.3.5"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

		      <xsl:call-template name="gender"/>

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

          <xsl:call-template name="project-partner-part2"/>

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
          <!-- <fo:block break-after='page'/> -->
				  
					<!-- <xsl:call-template name="title">
						<xsl:with-param name="title" select="'Workpackage - Task Test'"/>
						<xsl:with-param name="fontSize" select="$font.size.title"/>
					</xsl:call-template> -->

					<!-- <xsl:call-template name="work-package-task-test"/> -->

					<!-- <xsl:call-template name="title">
						<xsl:with-param name="title" select="'Target group - Event Test'"/>
						<xsl:with-param name="fontSize" select="$font.size.title"/>
					</xsl:call-template> -->

					<!-- <xsl:call-template name="target-group-event-test"/> -->

				</fo:flow>

			</fo:page-sequence>

		</fo:root>
	</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE PROJECT ENTITY -->	
<!--########################################################################-->

 <!-- TODO: adjust indentation of all code -->

	<xsl:template name="project">

		<xsl:call-template name="key-value-single">
			<xsl:with-param name="key" select="'Akronym'"/>
			<xsl:with-param name="value" select="cerif:Acronym"/>
		</xsl:call-template>

		<xsl:call-template name="key-period">
			<xsl:with-param name="key" select="$lang.term"/>
			<xsl:with-param name="startdate" select="cerif:StartDate"/>
			<xsl:with-param name="enddate" select="cerif:EndDate"/>
			<xsl:with-param name="addRuler" select="'false'"/>
		</xsl:call-template>
	
		<xsl:call-template name="key-value-single">
			<xsl:with-param name="key" select="$lang.duration.month"/>
			<xsl:with-param name="value" select="cerif:Duration"/>
			<xsl:with-param name="addRuler" select="'false'"/>
		</xsl:call-template>

		<xsl:call-template name="key-value-single">
			<xsl:with-param name="key" select="$lang.funding.call"/>
			<xsl:with-param name="value" select="cerif:FundingCall"/>
			<xsl:with-param name="addRuler" select="'false'"/>
		</xsl:call-template>

		<xsl:call-template name="key-value-single">
			<xsl:with-param name="key" select="$lang.funding.programme"/>
			<xsl:with-param name="value" select="cerif:FundingProgramme"/>
			<xsl:with-param name="addRuler" select="'false'"/>
		</xsl:call-template>			
	
	</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE PROJECT PARTNER ENTITY -->	
<!--########################################################################-->

<!--==============================================================-->	
<!-- part #1 for section 1 -->
<!--==============================================================-->

	<xsl:template name="project-partner-part1">

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
						<xsl:with-param name="fontSize" select="$font.size.key"/>
						<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
						<xsl:with-param name="addRuler" select="'false'"/>
					</xsl:call-template>									

					<xsl:call-template name="value-single"> <!-- TODO: not shown -->
						<xsl:with-param name="value" select="cerif:Import/cerif:ParentOrganisation"/>
						<xsl:with-param name="addRuler" select="'false'"/>
				  </xsl:call-template>	

					<xsl:call-template name="value-single"> 
						<xsl:with-param name="value" select="cerif:Import/cerif:OrganisationName"/>
						<xsl:with-param name="addRuler" select="'false'"/>
				  </xsl:call-template>					

					<xsl:call-template name="value-postcode-city-country">
						<xsl:with-param name="postcode" select="cerif:Import/cerif:PostCode"/>
						<xsl:with-param name="city" select="cerif:Import/cerif:City"/>
						<xsl:with-param name="country" select="cerif:Import/cerif:Country"/>
						<xsl:with-param name="addRuler" select="'false'"/>
				  </xsl:call-template>	

					<xsl:call-template name="value-comma-list"> 
						<xsl:with-param name="value" select="cerif:Import/cerif:WebAddress"/>
						<xsl:with-param name="addRuler" select="'false'"/>
				  </xsl:call-template>	

	        <xsl:if test="cerif:ImportLead/cerif:Name">
						<xsl:call-template name="vertical-gap">
							<xsl:with-param name="margin" select="$margin.top.gap"/>
						</xsl:call-template>
					</xsl:if>

					<xsl:call-template name="value-name-degree-gender">
						<xsl:with-param name="name" select="cerif:ImportLead/cerif:Name"/>
						<xsl:with-param name="degree" select="cerif:ImportLead/cerif:Degree"/>
						<xsl:with-param name="gender" select="cerif:ImportLead/cerif:Gender"/>
						<xsl:with-param name="addRuler" select="'false'"/>
				  </xsl:call-template>	

					<xsl:call-template name="value-single">
						<xsl:with-param name="value" select="cerif:ImportLead/cerif:Phone"/>
						<xsl:with-param name="addRuler" select="'false'"/>
				  </xsl:call-template>	

					<xsl:call-template name="value-single">
						<xsl:with-param name="value" select="cerif:ImportLead/cerif:EMail"/>
						<xsl:with-param name="addRuler" select="'false'"/>						
				  </xsl:call-template>	

				</fo:block> 		

			</xsl:for-each> 

	  </xsl:if>

	</xsl:template>

<!--==============================================================-->	
<!-- part #2 for section 5 -->
<!--==============================================================-->

	<xsl:template name="project-partner-part2">

		<xsl:if test="cerif:ProjectPartner">

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
						<xsl:with-param name="fontSize" select="$font.size.key"/>
						<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
					</xsl:call-template>									

					<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.organisation.form"/>
						<xsl:with-param name="value" select="cerif:Import/cerif:OrganisationType"/>
				  </xsl:call-template>						

					<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.projectpartner.easy-online"/>
						<xsl:with-param name="value" select="cerif:EasyOnlineImport"/>
				  </xsl:call-template>						

					<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.projectpartner.field-of-activity"/>
						<xsl:with-param name="value" select="cerif:FieldOfActivity"/>
				  </xsl:call-template>						

					<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.projectpartner.destatis"/>
						<xsl:with-param name="value" select="cerif:Destatis"/>
				  </xsl:call-template>	
					
					<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.projectpartner.nace"/>
						<xsl:with-param name="value" select="cerif:NACE"/>
				  </xsl:call-template>	
					
					<xsl:call-template name="key-value-comma-list">
						<xsl:with-param name="key" select="$lang.projectpartner.political-level"/>
						<xsl:with-param name="value" select="cerif:PoliticalLevel"/>
				  </xsl:call-template>						

				</fo:block> 		

			</xsl:for-each> 

	  </xsl:if>

	</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE IMPACT PATHWAY -->	
<!--########################################################################-->

  <xsl:template name="impact-pathway">

	  <xsl:if test="cerif:ImpactPathway">

    <!-- handle case if just one impact pathway exists -->
			<xsl:if test="count(cerif:ImpactPathway/cerif:Index) = 1">

			<xsl:call-template name="key-value-single">
				<xsl:with-param name="key" select="$lang.description"/>
				<xsl:with-param name="value" select="cerif:ImpactPathway/cerif:Index/cerif:Description"/>
			</xsl:call-template>

			</xsl:if> 

		<!-- handle case if more than one impact pathway exists -->
			<xsl:if test="count(cerif:ImpactPathway/cerif:Index) > 1"> 
				<xsl:for-each select="cerif:ImpactPathway/cerif:Index">

					<xsl:call-template name="key-only">  
						<xsl:with-param name="key" select="cerif:Title"/>
					</xsl:call-template>	

					<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.description"/>
						<xsl:with-param name="value" select="cerif:Description"/>
					</xsl:call-template>

				</xsl:for-each>

			</xsl:if> 

	  </xsl:if>
		
  </xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE PROJECT OBJECTIVE ENTITY -->	
<!--########################################################################-->

  <xsl:template name="project-objective">

		<xsl:call-template name="title-description">
			<xsl:with-param name="entity" select="cerif:ProjectObjective"/>
			<xsl:with-param name="entityIndex" select="cerif:ProjectObjective/cerif:Index"/>
		</xsl:call-template>	

	</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE INTERACTION OBJECTIVE ENTITY -->	
<!--########################################################################-->

  <xsl:template name="interaction-objective">

		<xsl:call-template name="title-description">
			<xsl:with-param name="entity" select="cerif:InteractionObjective"/>
			<xsl:with-param name="entityIndex" select="cerif:InteractionObjective/cerif:Index"/>
		</xsl:call-template>	

  </xsl:template>	

<!--########################################################################-->
<!-- TEMPLATE FOR THE CONTRIBUTION FUNDING PROGRAMME ENTITY -->	
<!--########################################################################-->

  <xsl:template name="contribution-funding-programme">

		<xsl:call-template name="title-description">
			<xsl:with-param name="entity" select="cerif:ContributionFundingProgramme"/>
			<xsl:with-param name="entityIndex" select="cerif:ContributionFundingProgramme/cerif:Index"/>
		</xsl:call-template>	

  </xsl:template>	

<!--########################################################################-->
<!-- TEMPLATE FOR THE STATE OF ART/SCIENCE ENTITY -->	
<!--########################################################################-->

  <xsl:template name="state-of-art">

		<xsl:call-template name="title-description">
			<xsl:with-param name="entity" select="cerif:StateOfArt"/>
			<xsl:with-param name="entityIndex" select="cerif:StateOfArt/cerif:Index"/>
		</xsl:call-template>	

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
				<fo:table-column column-width="16.166%"/>
				<fo:table-column column-width="16.166%"/>
				<fo:table-column column-width="16.166%"/>
				<fo:table-column column-width="16.166%"/>
				<fo:table-column column-width="16.166%"/>
				<fo:table-column column-width="16.166%"/>				
				<fo:table-column column-width="proportional-column-width(1)"/>  <!-- this is also part of central alignment approach --> 
																																			  <!-- in addition the columns need to be numbered, omitting the first and last column --> 
				<!-- table header -->
				<fo:table-header>

          <!-- event -->
					<fo:table-cell column-number="2" 
												 border-width="{$width.border}"
												 border-color="{$colour.border}"
												 border-style="{$style.border}">
						<fo:block text-align="center" 
											font-size="{$font.size.key}"
											font-weight="{$font.weight.key-value}"
											margin-top="{$margin.top.main}"
											padding-before="{$padding.before.border}"
											padding-after="{$padding.after.border}"
											padding-start="{$padding.start.border}"
											padding-end="{$padding.end.border}"
											margin-left="{$margin.left.border}"
											margin-right="{$margin.right.border}">	

							<xsl:variable name="imageFile" select="concat('file:',$imageDirectory,'event.png')"/>

							<fo:external-graphic content-width="{$width.icon.overview}">  
								<xsl:attribute name="src">
									<xsl:value-of select="$imageFile"/>
								</xsl:attribute>
							</fo:external-graphic>

							<fo:inline vertical-align="top">
							   <xsl:text>    </xsl:text> 
							  <xsl:value-of select="count(cerif:Event/cerif:Index)"/>
              </fo:inline>

						</fo:block>
					</fo:table-cell>

          <!-- process event -->
					<fo:table-cell column-number="3" 
												 border-width="{$width.border}"
												 border-color="{$colour.border}"
												 border-style="{$style.border}">
						<fo:block text-align="center" 
											font-size="{$font.size.key}"
											font-weight="{$font.weight.key-value}"
											margin-top="{$margin.top.main}"
											padding-before="{$padding.before.border}"
											padding-after="{$padding.after.border}"
											padding-start="{$padding.start.border}"
											padding-end="{$padding.end.border}"
											margin-left="{$margin.left.border}"
											margin-right="{$margin.right.border}">	

							<xsl:variable name="imageFile" select="concat('file:',$imageDirectory,'process.png')"/>

							<fo:external-graphic content-width="{$width.icon.overview}">  <!-- content-height="scale-to-fit" scaling="non-uniform" -->
								<xsl:attribute name="src">
									<xsl:value-of select="$imageFile"/>
								</xsl:attribute>
							</fo:external-graphic>

							<fo:inline vertical-align="top">
							  <xsl:text>    </xsl:text> 
							  <xsl:value-of select="count(cerif:ProcessEvent/cerif:Index)"/>
              </fo:inline>

						</fo:block>
					</fo:table-cell>

          <!-- physical object -->
					<fo:table-cell column-number="4" 
												 border-width="{$width.border}"
												 border-color="{$colour.border}"
												 border-style="{$style.border}">
						<fo:block text-align="center" 
											font-size="{$font.size.key}"
											font-weight="{$font.weight.key-value}"
											margin-top="{$margin.top.main}"
											padding-before="{$padding.before.border}"
											padding-after="{$padding.after.border}"
											padding-start="{$padding.start.border}"
											padding-end="{$padding.end.border}"
											margin-left="{$margin.left.border}"
											margin-right="{$margin.right.border}">	

							<xsl:variable name="imageFile" select="concat('file:',$imageDirectory,'object-material.png')"/>

							<fo:external-graphic content-width="{$width.icon.overview}">  <!-- content-height="scale-to-fit" scaling="non-uniform" -->
								<xsl:attribute name="src">
									<xsl:value-of select="$imageFile"/>
								</xsl:attribute>
							</fo:external-graphic>

							<fo:inline vertical-align="top">
							  <xsl:text>    </xsl:text> 
							  <xsl:value-of select="count(cerif:PhysicalObject/cerif:Index)"/>
              </fo:inline>

						</fo:block>
					</fo:table-cell>

          <!-- planned publication -->
					<fo:table-cell column-number="5" 
												 border-width="{$width.border}"
												 border-color="{$colour.border}"
												 border-style="{$style.border}">
						<fo:block text-align="center" 
											font-size="{$font.size.key}"
											font-weight="{$font.weight.key-value}"
											margin-top="{$margin.top.main}"
											padding-before="{$padding.before.border}"
											padding-after="{$padding.after.border}"
											padding-start="{$padding.start.border}"
											padding-end="{$padding.end.border}"
											margin-left="{$margin.left.border}"
											margin-right="{$margin.right.border}">	

							<xsl:variable name="imageFile" select="concat('file:',$imageDirectory,'publication.png')"/>

							<fo:external-graphic content-width="{$width.icon.overview}">  <!-- content-height="scale-to-fit" scaling="non-uniform" -->
								<xsl:attribute name="src">
									<xsl:value-of select="$imageFile"/>
								</xsl:attribute>
							</fo:external-graphic>

							<fo:inline vertical-align="top">
							  <xsl:text>    </xsl:text> 
							  <xsl:value-of select="count(cerif:PlannedPublication/cerif:Index)"/>
              </fo:inline>

						</fo:block>
					</fo:table-cell>

          <!-- publication -->
					<fo:table-cell column-number="6" 
												 border-width="{$width.border}"
												 border-color="{$colour.border}"
												 border-style="{$style.border}">
						<fo:block text-align="center" 
											font-size="{$font.size.key}"
											font-weight="{$font.weight.key-value}"
											margin-top="{$margin.top.main}"
											padding-before="{$padding.before.border}"
											padding-after="{$padding.after.border}"
											padding-start="{$padding.start.border}"
											padding-end="{$padding.end.border}"
											margin-left="{$margin.left.border}"
											margin-right="{$margin.right.border}">	

							<xsl:variable name="imageFile" select="concat('file:',$imageDirectory,'publication.png')"/>

							<fo:external-graphic content-width="{$width.icon.overview}">  <!-- content-height="scale-to-fit" scaling="non-uniform" -->
								<xsl:attribute name="src">
									<xsl:value-of select="$imageFile"/>
								</xsl:attribute>
							</fo:external-graphic>

							<fo:inline vertical-align="top">
							  <xsl:text>    </xsl:text> 
							  <xsl:value-of select="count(cerif:Publication/cerif:Index)"/>
              </fo:inline>

						</fo:block>
					</fo:table-cell>

          <!-- product -->
					<fo:table-cell column-number="7" 
												 border-width="{$width.border}"
												 border-color="{$colour.border}"
												 border-style="{$style.border}">
						<fo:block text-align="center" 
											font-size="{$font.size.key}"
											font-weight="{$font.weight.key-value}"
											margin-top="{$margin.top.main}"
											padding-before="{$padding.before.border}"
											padding-after="{$padding.after.border}"
											padding-start="{$padding.start.border}"
											padding-end="{$padding.end.border}"
											margin-left="{$margin.left.border}"
											margin-right="{$margin.right.border}">	

							<xsl:variable name="imageFile" select="concat('file:',$imageDirectory,'object-material.png')"/>

							<fo:external-graphic content-width="{$width.icon.overview}">  <!-- content-height="scale-to-fit" scaling="non-uniform" -->
								<xsl:attribute name="src">
									<xsl:value-of select="$imageFile"/>
								</xsl:attribute>
							</fo:external-graphic>

							<fo:inline vertical-align="top">
							  <xsl:text>    </xsl:text> 
							  <xsl:value-of select="count(cerif:Product/cerif:Index)"/>
              </fo:inline>

						</fo:block>
					</fo:table-cell>

				</fo:table-header>

				<!-- table body -->
				<fo:table-body>					
					<fo:table-row>

          	<!-- event -->
						<fo:table-cell column-number="2"
													 border-width="{$width.border}"
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
								<xsl:value-of select="$lang.overview.event"/>				
							</fo:block>
						</fo:table-cell>

          	<!-- process event -->
						<fo:table-cell column-number="3"
													 border-width="{$width.border}"
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
								<xsl:value-of select="$lang.overview.process-event"/>
							</fo:block>
						</fo:table-cell>

      			<!-- physical object -->
						<fo:table-cell column-number="4"
													 border-width="{$width.border}"
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
								<xsl:value-of select="$lang.overview.physical-object"/>				
							</fo:block>
						</fo:table-cell>

      			<!-- planned publication -->
						<fo:table-cell column-number="5"
													 border-width="{$width.border}"
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
								<xsl:value-of select="$lang.overview.planned-publication"/>				
							</fo:block>
						</fo:table-cell>

          	<!-- publication -->
						<fo:table-cell column-number="6"
													 border-width="{$width.border}"
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
								<xsl:value-of select="$lang.overview.publication"/>				
							</fo:block>
						</fo:table-cell>

          	<!-- product -->
						<fo:table-cell column-number="7"
													 border-width="{$width.border}"
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
								<xsl:value-of select="$lang.overview.product"/>				
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

		<xsl:if test="count(cerif:Milestone/cerif:Index) > 0">

			<fo:block	margin-top="{$margin.top.main}"
								padding-before="{$padding.before.border}"
								padding-after="{$padding.after.border}"
								padding-start="{$padding.start.border}"
								padding-end="{$padding.end.border}">

  			<fo:table table-layout="fixed" vertical-align="middle">

					<!-- define the table columns -->
					<fo:table-column column-width="proportional-column-width(1)"/>	<!-- this construct is used to align the table in a centred way -->							
					<fo:table-column column-width="12.0%"/>
					<fo:table-column column-width="30.0%"/>
					<fo:table-column column-width="55.0%"/>
					<fo:table-column column-width="proportional-column-width(1)"/>  <!-- this is also part of central alignment approach --> 

  				<!-- table header -->
	  			<fo:table-header>

            <!-- date-->
						<fo:table-cell column-number="2" 
													 border-width="{$width.border}"
													 border-color="{$colour.border}"
													 border-style="{$style.border}">
							<fo:block text-align="center" 
												font-size="{$font.size.key}"
												font-weight="{$font.weight.key-value}"
												margin-top="{$margin.top.main}"
												padding-before="{$padding.before.border}"
												padding-after="{$padding.after.border}"
												padding-start="{$padding.start.border}"
												padding-end="{$padding.end.border}"
												margin-left="{$margin.left.border}"
												margin-right="{$margin.right.border}">
								<xsl:value-of select="$lang.date"/>
							</fo:block>
						</fo:table-cell>

            <!-- title -->
						<fo:table-cell column-number="3" 
													 border-width="{$width.border}"
													 border-color="{$colour.border}"
													 border-style="{$style.border}">
							<fo:block text-align="left" 
												font-size="{$font.size.key}"
												font-weight="{$font.weight.key-value}"
												margin-top="{$margin.top.main}"
												padding-before="{$padding.before.border}"
												padding-after="{$padding.after.border}"
												padding-start="{$padding.start.border}"
												padding-end="{$padding.end.border}"
												margin-left="{$margin.left.border}"
												margin-right="{$margin.right.border}">
								<xsl:value-of select="$lang.title"/>
							</fo:block>
						</fo:table-cell>

            <!-- description -->
						<fo:table-cell column-number="4" 
													 border-width="{$width.border}"
													 border-color="{$colour.border}"
													 border-style="{$style.border}">
							<fo:block text-align="left" 
												font-size="{$font.size.key}"
												font-weight="{$font.weight.key-value}"
												margin-top="{$margin.top.main}"
												padding-before="{$padding.before.border}"
												padding-after="{$padding.after.border}"
												padding-start="{$padding.start.border}"
												padding-end="{$padding.end.border}"
												margin-left="{$margin.left.border}"
												margin-right="{$margin.right.border}">
								<xsl:value-of select="$lang.description"/>
							</fo:block>
						</fo:table-cell>

					</fo:table-header>

          <!-- table body -->
					<fo:table-body>					
					  <xsl:for-each select="cerif:Milestone/cerif:Index">

							<fo:table-row>

								<!-- date -->
								<fo:table-cell column-number="2"
												 			 border-width="{$width.border}"
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
										<xsl:value-of select="cerif:EndDate"/>
									</fo:block>
								</fo:table-cell>

								<!-- title -->
								<fo:table-cell column-number="3"
												 			 border-width="{$width.border}"
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

								<!-- description -->
								<fo:table-cell column-number="4"
												 			 border-width="{$width.border}"
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

		</xsl:if>

	</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE WORK PACKAGES -->	
<!--########################################################################-->

  <xsl:template name="work-package">

	  <xsl:if test="cerif:WorkPackage">
	
			<xsl:for-each select="cerif:WorkPackage/cerif:Index">
	
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
						<!-- table header with title -->
						<fo:table-header>
							<fo:table-cell column-number="2" 
														border-width="{$width.border}"
														border-color="{$colour.border}"
														border-style="{$style.border}">
								<fo:block text-align="left" 
													font-size="{$font.size.key}"
													font-weight="{$font.weight.key-value}"
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
						</fo:table-header>
			
						<!-- table body -->
						<fo:table-body>					

							<!-- description -->
							<fo:table-row>

								<fo:table-cell column-number="2"
															border-width="{$width.border}"
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

										<xsl:call-template name="key-value-single-below">	
											<xsl:with-param name="key" select="$lang.workpackage.description"/>
											<xsl:with-param name="value" select="cerif:Description"/>
											<xsl:with-param name="checkValue" select="'false'"/>
										</xsl:call-template>

										<xsl:call-template name="key-value-single-below">	
											<xsl:with-param name="key" select="$lang.workpackage.requirement"/>
											<xsl:with-param name="value" select="cerif:Requirement"/>
											<xsl:with-param name="checkValue" select="'false'"/>											
										</xsl:call-template>

										<xsl:call-template name="key-value-single-below">	
											<xsl:with-param name="key" select="$lang.workpackage.responsible-organisation"/>
											<xsl:with-param name="value" select="cerif:ResponsibleOrganisation"/>
											<xsl:with-param name="checkValue" select="'false'"/>
											<xsl:with-param name="addRuler" select="'false'"/>											
										</xsl:call-template>

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
<!-- TEMPLATE FOR THE RESEARCH WORK -->	
<!--########################################################################-->

  <xsl:template name="research-work">

	  <xsl:if test="cerif:WorkPackage">
	
			<xsl:for-each select="cerif:WorkPackage/cerif:Index">
	
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
						<!-- table header with title -->
						<fo:table-header>
							<fo:table-cell column-number="2" 
														border-width="{$width.border}"
														border-color="{$colour.border}"
														border-style="{$style.border}">
								<fo:block text-align="left" 
													font-size="{$font.size.key}"
													font-weight="{$font.weight.key-value}"
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
						</fo:table-header>
			
						<!-- table body -->
						<fo:table-body>	

							<xsl:if test="cerif:Task">

								<xsl:for-each select="cerif:Task">

							<!-- description -->
									<fo:table-row>							

										<fo:table-cell column-number="2"
																border-width="{$width.border}"
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

														<xsl:call-template name="value-single">	
															<xsl:with-param name="value" select="cerif:Index/cerif:Title"/>
															<xsl:with-param name="fontSize" select="$font.size.key"/>
															<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
														</xsl:call-template>

														<xsl:call-template name="key-value-single">
															<xsl:with-param name="key" select="$lang.innovationidea.description"/>
															<xsl:with-param name="value" select="cerif:Index/cerif:Description"/>
														</xsl:call-template> 

												</fo:block>

										</fo:table-cell>

									</fo:table-row>

								</xsl:for-each> 

							</xsl:if>

						</fo:table-body>				

					</fo:table>								
			
				</fo:block> 		
		
		  </xsl:for-each> 
	
		</xsl:if>
	
	</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE EXPLOITATION EFFECT OVERVIEW -->	
<!--########################################################################-->

<xsl:template name="exploitation-effect-overview">

	<fo:block	margin-top="{$margin.top.main}"
						padding-before="{$padding.before.border}"
						padding-after="{$padding.after.border}"
						padding-start="{$padding.start.border}"
						padding-end="{$padding.end.border}">

		<fo:table table-layout="fixed" vertical-align="middle">

			<!-- define the table columns -->
			<fo:table-column column-width="proportional-column-width(1)"/>	<!-- this construct is used to align the table in a centred way -->							
			<fo:table-column column-width="10.777%"/>
			<fo:table-column column-width="10.777%"/>
			<fo:table-column column-width="10.777%"/>
			<fo:table-column column-width="10.777%"/>
			<fo:table-column column-width="10.777%"/>
			<fo:table-column column-width="10.777%"/>
			<fo:table-column column-width="10.777%"/>
			<fo:table-column column-width="10.777%"/>
			<fo:table-column column-width="10.777%"/>																								
			<fo:table-column column-width="proportional-column-width(1)"/>  <!-- this is also part of central alignment approach --> 
																																			<!-- in addition the columns need to be numbered, omitting the first and last column --> 
			<!-- table header -->
			<fo:table-header>

				<!-- innovation potential -->
				<fo:table-cell column-number="2" 
												border-width="{$width.border}"
												border-color="{$colour.border}"
												border-style="{$style.border}">
					<fo:block text-align="center" 
										font-size="{$font.size.key}"
										font-weight="{$font.weight.key-value}"
										margin-top="{$margin.top.main}"
										padding-before="{$padding.before.border}"
										padding-after="{$padding.after.border}"
										padding-start="{$padding.start.border}"
										padding-end="{$padding.end.border}"
										margin-left="{$margin.left.border}"
										margin-right="{$margin.right.border}">	

						<xsl:variable name="imageFile" select="concat('file:',$imageDirectory,'link.png')"/>

						<fo:external-graphic content-width="{$width.icon.overview}">  
							<xsl:attribute name="src">
								<xsl:value-of select="$imageFile"/>
							</xsl:attribute>
						</fo:external-graphic>

						<fo:inline vertical-align="top">
								<xsl:text>    </xsl:text> 
							<xsl:value-of select="count(cerif:InnovationPotential/cerif:Index)"/>
						</fo:inline>

					</fo:block>
				</fo:table-cell>

				<!-- innovation idea -->
				<fo:table-cell column-number="3" 
												border-width="{$width.border}"
												border-color="{$colour.border}"
												border-style="{$style.border}">
					<fo:block text-align="center" 
										font-size="{$font.size.key}"
										font-weight="{$font.weight.key-value}"
										margin-top="{$margin.top.main}"
										padding-before="{$padding.before.border}"
										padding-after="{$padding.after.border}"
										padding-start="{$padding.start.border}"
										padding-end="{$padding.end.border}"
										margin-left="{$margin.left.border}"
										margin-right="{$margin.right.border}">	

						<xsl:variable name="imageFile" select="concat('file:',$imageDirectory,'process.png')"/>

						<fo:external-graphic content-width="{$width.icon.overview}">  <!-- content-height="scale-to-fit" scaling="non-uniform" -->
							<xsl:attribute name="src">
								<xsl:value-of select="$imageFile"/>
							</xsl:attribute>
						</fo:external-graphic>

						<fo:inline vertical-align="top">
							<xsl:text>    </xsl:text> 
							<xsl:value-of select="count(cerif:InnovationIdea/cerif:Index)"/>
						</fo:inline>

					</fo:block>
				</fo:table-cell>

				<!-- application -->
				<fo:table-cell column-number="4" 
												border-width="{$width.border}"
												border-color="{$colour.border}"
												border-style="{$style.border}">
					<fo:block text-align="center" 
										font-size="{$font.size.key}"
										font-weight="{$font.weight.key-value}"
										margin-top="{$margin.top.main}"
										padding-before="{$padding.before.border}"
										padding-after="{$padding.after.border}"
										padding-start="{$padding.start.border}"
										padding-end="{$padding.end.border}"
										margin-left="{$margin.left.border}"
										margin-right="{$margin.right.border}">	

						<xsl:variable name="imageFile" select="concat('file:',$imageDirectory,'object-material.png')"/>

						<fo:external-graphic content-width="{$width.icon.overview}">  <!-- content-height="scale-to-fit" scaling="non-uniform" -->
							<xsl:attribute name="src">
								<xsl:value-of select="$imageFile"/>
							</xsl:attribute>
						</fo:external-graphic>

						<fo:inline vertical-align="top">
							<xsl:text>    </xsl:text> 
							<xsl:value-of select="count(cerif:Application/cerif:Index)"/>
						</fo:inline>

					</fo:block>
				</fo:table-cell>

				<!-- unexpected result -->
				<fo:table-cell column-number="5" 
												border-width="{$width.border}"
												border-color="{$colour.border}"
												border-style="{$style.border}">
					<fo:block text-align="center" 
										font-size="{$font.size.key}"
										font-weight="{$font.weight.key-value}"
										margin-top="{$margin.top.main}"
										padding-before="{$padding.before.border}"
										padding-after="{$padding.after.border}"
										padding-start="{$padding.start.border}"
										padding-end="{$padding.end.border}"
										margin-left="{$margin.left.border}"
										margin-right="{$margin.right.border}">	

						<xsl:variable name="imageFile" select="concat('file:',$imageDirectory,'task.png')"/>

						<fo:external-graphic content-width="{$width.icon.overview}">  <!-- content-height="scale-to-fit" scaling="non-uniform" -->
							<xsl:attribute name="src">
								<xsl:value-of select="$imageFile"/>
							</xsl:attribute>
						</fo:external-graphic>

						<fo:inline vertical-align="top">
							<xsl:text>    </xsl:text> 
							<xsl:value-of select="count(cerif:UnexpectedResult/cerif:Index)"/>
						</fo:inline>

					</fo:block>
				</fo:table-cell>

				<!-- patent -->
				<fo:table-cell column-number="6" 
												border-width="{$width.border}"
												border-color="{$colour.border}"
												border-style="{$style.border}">
					<fo:block text-align="center" 
										font-size="{$font.size.key}"
										font-weight="{$font.weight.key-value}"
										margin-top="{$margin.top.main}"
										padding-before="{$padding.before.border}"
										padding-after="{$padding.after.border}"
										padding-start="{$padding.start.border}"
										padding-end="{$padding.end.border}"
										margin-left="{$margin.left.border}"
										margin-right="{$margin.right.border}">	

						<xsl:variable name="imageFile" select="concat('file:',$imageDirectory,'publication.png')"/>

						<fo:external-graphic content-width="{$width.icon.overview}">  <!-- content-height="scale-to-fit" scaling="non-uniform" -->
							<xsl:attribute name="src">
								<xsl:value-of select="$imageFile"/>
							</xsl:attribute>
						</fo:external-graphic>

						<fo:inline vertical-align="top">
							<xsl:text>    </xsl:text> 
							<xsl:value-of select="count(cerif:Patent/cerif:Index)"/>
						</fo:inline>

					</fo:block>
				</fo:table-cell>

				<!-- spinoff -->
				<fo:table-cell column-number="7" 
												border-width="{$width.border}"
												border-color="{$colour.border}"
												border-style="{$style.border}">
					<fo:block text-align="center" 
										font-size="{$font.size.key}"
										font-weight="{$font.weight.key-value}"
										margin-top="{$margin.top.main}"
										padding-before="{$padding.before.border}"
										padding-after="{$padding.after.border}"
										padding-start="{$padding.start.border}"
										padding-end="{$padding.end.border}"
										margin-left="{$margin.left.border}"
										margin-right="{$margin.right.border}">	

						<xsl:variable name="imageFile" select="concat('file:',$imageDirectory,'work-package.png')"/>

						<fo:external-graphic content-width="{$width.icon.overview}">  <!-- content-height="scale-to-fit" scaling="non-uniform" -->
							<xsl:attribute name="src">
								<xsl:value-of select="$imageFile"/>
							</xsl:attribute>
						</fo:external-graphic>

						<fo:inline vertical-align="top">
							<xsl:text>    </xsl:text> 
							<xsl:value-of select="count(cerif:Spinoff/cerif:Index)"/>
						</fo:inline>

					</fo:block>
				</fo:table-cell>

				<!-- further steps -->
				<fo:table-cell column-number="8" 
												border-width="{$width.border}"
												border-color="{$colour.border}"
												border-style="{$style.border}">
					<fo:block text-align="center" 
										font-size="{$font.size.key}"
										font-weight="{$font.weight.key-value}"
										margin-top="{$margin.top.main}"
										padding-before="{$padding.before.border}"
										padding-after="{$padding.after.border}"
										padding-start="{$padding.start.border}"
										padding-end="{$padding.end.border}"
										margin-left="{$margin.left.border}"
										margin-right="{$margin.right.border}">	

						<xsl:variable name="imageFile" select="concat('file:',$imageDirectory,'object-material.png')"/>

						<fo:external-graphic content-width="{$width.icon.overview}">  <!-- content-height="scale-to-fit" scaling="non-uniform" -->
							<xsl:attribute name="src">
								<xsl:value-of select="$imageFile"/>
							</xsl:attribute>
						</fo:external-graphic>

						<fo:inline vertical-align="top">
							<xsl:text>    </xsl:text> 
							<xsl:value-of select="count(cerif:Step/cerif:Index)"/>
						</fo:inline>

					</fo:block>
				</fo:table-cell>

				<!-- award -->
				<fo:table-cell column-number="9" 
												border-width="{$width.border}"
												border-color="{$colour.border}"
												border-style="{$style.border}">
					<fo:block text-align="center" 
										font-size="{$font.size.key}"
										font-weight="{$font.weight.key-value}"
										margin-top="{$margin.top.main}"
										padding-before="{$padding.before.border}"
										padding-after="{$padding.after.border}"
										padding-start="{$padding.start.border}"
										padding-end="{$padding.end.border}"
										margin-left="{$margin.left.border}"
										margin-right="{$margin.right.border}">	

						<xsl:variable name="imageFile" select="concat('file:',$imageDirectory,'publication.png')"/>

						<fo:external-graphic content-width="{$width.icon.overview}">  <!-- content-height="scale-to-fit" scaling="non-uniform" -->
							<xsl:attribute name="src">
								<xsl:value-of select="$imageFile"/>
							</xsl:attribute>
						</fo:external-graphic>

						<fo:inline vertical-align="top">
							<xsl:text>    </xsl:text> 
							<xsl:value-of select="count(cerif:Award/cerif:Index)"/>
						</fo:inline>

					</fo:block>
				</fo:table-cell>

				<!-- open research question -->
				<fo:table-cell column-number="10" 
												border-width="{$width.border}"
												border-color="{$colour.border}"
												border-style="{$style.border}">
					<fo:block text-align="center" 
										font-size="{$font.size.key}"
										font-weight="{$font.weight.key-value}"
										margin-top="{$margin.top.main}"
										padding-before="{$padding.before.border}"
										padding-after="{$padding.after.border}"
										padding-start="{$padding.start.border}"
										padding-end="{$padding.end.border}"
										margin-left="{$margin.left.border}"
										margin-right="{$margin.right.border}">	

						<xsl:variable name="imageFile" select="concat('file:',$imageDirectory,'indirect-target-group.png')"/>

						<fo:external-graphic content-width="{$width.icon.overview}">  <!-- content-height="scale-to-fit" scaling="non-uniform" -->
							<xsl:attribute name="src">
								<xsl:value-of select="$imageFile"/>
							</xsl:attribute>
						</fo:external-graphic>

						<fo:inline vertical-align="top">
							<xsl:text>    </xsl:text> 
							<xsl:value-of select="count(cerif:OpenResearchQuestion/cerif:Index)"/>
						</fo:inline>

					</fo:block>
				</fo:table-cell>

			</fo:table-header>

			<!-- table body -->
			<fo:table-body>					
				<fo:table-row>

					<!-- innovation potential -->
					<fo:table-cell column-number="2"
													border-width="{$width.border}"
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
							<xsl:value-of select="$lang.overview.innovation-potential"/> 				
						</fo:block>
					</fo:table-cell>

					<!-- innovation idea -->
					<fo:table-cell column-number="3"
													border-width="{$width.border}"
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
							<xsl:value-of select="$lang.overview.innovation-idea"/>
						</fo:block>
					</fo:table-cell>

					<!-- application -->
					<fo:table-cell column-number="4"
													border-width="{$width.border}"
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
							<xsl:value-of select="$lang.overview.application"/>
						</fo:block>
					</fo:table-cell>

					<!-- unexpected result -->
					<fo:table-cell column-number="5"
													border-width="{$width.border}"
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
							<xsl:value-of select="$lang.overview.unexpected-result"/>
						</fo:block>
					</fo:table-cell>

					<!-- patent -->
					<fo:table-cell column-number="6"
													border-width="{$width.border}"
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
							<xsl:value-of select="$lang.overview.patent"/>
						</fo:block>
					</fo:table-cell>

					<!-- spinoff -->
					<fo:table-cell column-number="7"
													border-width="{$width.border}"
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
							<xsl:value-of select="$lang.overview.spinoff"/>
						</fo:block>
					</fo:table-cell>

					<!-- further steps -->
					<fo:table-cell column-number="8"
													border-width="{$width.border}"
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
							<xsl:value-of select="$lang.overview.further-steps"/>
						</fo:block>
					</fo:table-cell>

					<!-- award -->
					<fo:table-cell column-number="9"
													border-width="{$width.border}"
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
							<xsl:value-of select="$lang.overview.award"/>
						</fo:block>
					</fo:table-cell>

					<!-- open research question -->
					<fo:table-cell column-number="10"
													border-width="{$width.border}"
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
							<xsl:value-of select="$lang.overview.open-research-question"/>
						</fo:block>
					</fo:table-cell>

				</fo:table-row>

			</fo:table-body>				

		</fo:table>								

	</fo:block> 

</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE INNOVATION POTENTIAL -->	
<!--########################################################################-->

<xsl:template name="innovation-potential">

		<xsl:if test="cerif:InnovationPotential">

			<xsl:for-each select="cerif:InnovationPotential/cerif:Index">

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
						<xsl:with-param name="fontSize" select="$font.size.key"/>
						<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
					</xsl:call-template>									

					<xsl:call-template name="key-value-single">	
						<xsl:with-param name="key" select="$lang.innovationpotential.type"/>
						<xsl:with-param name="value" select="cerif:Type"/>
				  </xsl:call-template>

					<xsl:call-template name="key-value-line-list">	
						<xsl:with-param name="key" select="$lang.innovationpotential.targetgroup"/>
						<xsl:with-param name="value" select="cerif:TargetGroup"/>
				  </xsl:call-template>

				 	<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.innovationpotential.description"/>
						<xsl:with-param name="value" select="cerif:Description"/>
				  </xsl:call-template> 

				 	<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.innovationpotential.applicability-novelty"/>
						<xsl:with-param name="value" select="cerif:Applicability/cerif:Novelty"/>
				  </xsl:call-template> 

				 	<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.innovationpotential.applicability-efficiency"/>
						<xsl:with-param name="value" select="cerif:Applicability/cerif:Efficiency"/>
				  </xsl:call-template>

				 	<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.innovationpotential.applicability-practicability"/>
						<xsl:with-param name="value" select="cerif:Applicability/cerif:Practicability"/>
				  </xsl:call-template>

				 	<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.innovationpotential.applicability-requirements"/>
						<xsl:with-param name="value" select="cerif:Applicability/cerif:Requirements"/>
				  </xsl:call-template>

			 	<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.innovationpotential.applicability-prospects"/>
						<xsl:with-param name="value" select="cerif:Applicability/cerif:Prospects"/>
				  </xsl:call-template>

			 	<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.innovationpotential.regionalscope-primaryfield"/>
						<xsl:with-param name="value" select="cerif:RegionalScope/cerif:PrimaryField"/>
				  </xsl:call-template>

			 	<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.innovationpotential.regionalscope-laterdissimination"/>
						<xsl:with-param name="value" select="cerif:RegionalScope/cerif:LaterDissimination"/>
				  </xsl:call-template>

			 	<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.innovationpotential.regionalscope-description"/>
						<xsl:with-param name="value" select="cerif:RegionalScope/cerif:Description"/>
				  </xsl:call-template>

			 	<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.innovationpotential.srltrl-developmentprojectstart"/>
						<xsl:with-param name="value" select="cerif:SRLTRL/cerif:DevelopmentProjectStart"/>
				  </xsl:call-template>

			 	<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.innovationpotential.srltrl-developmentprojectend"/>
						<xsl:with-param name="value" select="cerif:SRLTRL/cerif:DevelopmentProjectEnd"/>
				  </xsl:call-template>

			 	<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.innovationpotential.srltrl-selection"/>
						<xsl:with-param name="value" select="cerif:SRLTRL/cerif:Selection"/>
				  </xsl:call-template>

				  			 	<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.innovationpotential.srltrl-readinessprojectstart"/>
						<xsl:with-param name="value" select="cerif:SRLTRL/cerif:ReadinessProjectStart"/>
				  </xsl:call-template>

				  			 	<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.innovationpotential.srltrl-readinessprojectend"/>
						<xsl:with-param name="value" select="cerif:SRLTRL/cerif:ReadinessProjectEnd"/>
				  </xsl:call-template>

				</fo:block> 		

			</xsl:for-each> 

		  </xsl:if>

	</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE INNOVATION IDEAS -->	
<!--########################################################################-->

	<xsl:template name="innovation-idea">

		<xsl:if test="cerif:InnovationIdea">

			<xsl:for-each select="cerif:InnovationIdea/cerif:Index">

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
						<xsl:with-param name="fontSize" select="$font.size.key"/>
						<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
					</xsl:call-template>									

					<xsl:call-template name="key-value-line-list">	
						<xsl:with-param name="key" select="$lang.innovationidea.type"/>
						<xsl:with-param name="value" select="cerif:Type"/>
				  </xsl:call-template>

				 	<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.innovationidea.description"/>
						<xsl:with-param name="value" select="cerif:Description"/>
				  </xsl:call-template> 

				</fo:block> 		

			</xsl:for-each> 

		  </xsl:if>

	</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE APPLICATIONS -->	
<!--########################################################################-->

	<xsl:template name="application">

		<xsl:if test="cerif:Application">

			<xsl:for-each select="cerif:Application/cerif:Index">

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
						<xsl:with-param name="value" select="cerif:ApplicationTitle"/>
						<xsl:with-param name="fontSize" select="$font.size.key"/>
						<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
					</xsl:call-template>									

					<xsl:call-template name="key-value-line-list">	
						<xsl:with-param name="key" select="$lang.application.related-innovation"/>
						<xsl:with-param name="value" select="cerif:RelatedInnovation"/>
				  </xsl:call-template>

				 	<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.application.unit"/>
						<xsl:with-param name="value" select="cerif:Unit"/>
				  </xsl:call-template> 

<!-- eingegebene eigene Einheiten werden im System nicht abgespeichert -->
				 	<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.application.free-unit"/>
						<xsl:with-param name="value" select="cerif:FreeUnit"/>
				  </xsl:call-template> 

						<fo:table>
									
							<fo:table-column column-number="1" column-width="15%"/>	
							<fo:table-column column-number="2" column-width="15%"/>	
							<fo:table-column column-number="3" column-width="30%"/>	
							<fo:table-column column-number="4" column-width="40%"/>	
							
							<fo:table-header>
								<fo:table-row>
								<fo:table-cell border-width="{$width.border}"
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
											<xsl:value-of select="$lang.application.reference-year"/>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell border-width="{$width.border}"
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
											<xsl:value-of select="$lang.application.quantity"/>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell border-width="{$width.border}"
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
											<xsl:value-of select="$lang.application.regionaloutreach"/>
									</fo:block>
								</fo:table-cell>

									<fo:table-cell border-width="{$width.border}"
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
											<xsl:value-of select="$lang.application.success-description"/>
									</fo:block>
								</fo:table-cell>				

								</fo:table-row>
							</fo:table-header>

							<fo:table-body>
								<xsl:for-each select="cerif:SuccessQuantification/cerif:Index">
								<fo:table-row>
									<fo:table-cell border-width="{$width.border}"
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
											<xsl:value-of select="cerif:ReferenceYear"/>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell border-width="{$width.border}"
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
											<xsl:value-of select="cerif:Quantity"/>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell border-width="{$width.border}"
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
											<xsl:value-of select="cerif:RegionalOutreach"/>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell border-width="{$width.border}"
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
											<xsl:value-of select="cerif:SuccessDescription"/>
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
<!-- TEMPLATE FOR THE UNEXPECTED RESULTS -->	
<!--########################################################################-->

	<xsl:template name="unexpected-result">

		<xsl:if test="cerif:UnexpectedResult">

			<xsl:for-each select="cerif:UnexpectedResult/cerif:Index">

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
						<xsl:with-param name="fontSize" select="$font.size.key"/>
						<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
					</xsl:call-template>									

					<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.unexpectedresult.resultdescription"/>
						<xsl:with-param name="value" select="cerif:ResultDescription"/>
				  </xsl:call-template>

				 	<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.unexpectedresult.statuspublication"/>
						<xsl:with-param name="value" select="cerif:Publication/cerif:Index/cerif:StatusPublication"/>
				  </xsl:call-template> 

					<xsl:call-template name="key-value-line-list">				
						<xsl:with-param name="key" select="$lang.unexpectedresult.linkpublication"/>
						<xsl:with-param name="value" select="cerif:Publication/cerif:Index/cerif:LinkPublication"/>
					</xsl:call-template>

				</fo:block> 		

			</xsl:for-each> 

		  </xsl:if>

	</xsl:template>




<!--########################################################################-->
<!-- TEMPLATE FOR THE PATENT ENTITY -->	
<!--########################################################################-->

	<xsl:template name="patent">

		<xsl:if test="cerif:Patent">

			<xsl:for-each select="cerif:Patent/cerif:Index">

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
						<xsl:with-param name="fontSize" select="$font.size.key"/>
						<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
					</xsl:call-template>									

					<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.patent.type"/>
						<xsl:with-param name="value" select="cerif:Type"/>
				  </xsl:call-template>						

					<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.patent.use"/>
						<xsl:with-param name="value" select="cerif:Use"/>
				  </xsl:call-template>						

					<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.patent.use-description"/>
						<xsl:with-param name="value" select="cerif:UseDescription"/>
				  </xsl:call-template>	

          <xsl:if test="cerif:Result/cerif:Holder">
						<xsl:call-template name="vertical-gap">
							<xsl:with-param name="margin" select="$margin.top.gap"/>
						</xsl:call-template>
          </xsl:if>

					<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.patent.registration-number"/>
						<xsl:with-param name="value" select="cerif:Result/cerif:RegistrationNumber"/>
				  </xsl:call-template>	

					<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.patent.registration-date"/>
						<xsl:with-param name="value" select="cerif:Result/cerif:RegistrationDate"/>
				  </xsl:call-template>	

					<xsl:call-template name="key-value-comma-list"> 
						<xsl:with-param name="key" select="$lang.patent.holder"/>
						<xsl:with-param name="value" select="cerif:Result/cerif:Holder"/>
				  </xsl:call-template>	

          <xsl:if test="cerif:Contributor/cerif:Index">
						<xsl:call-template name="vertical-gap">
							<xsl:with-param name="margin" select="$margin.top.gap"/>
						</xsl:call-template>
          </xsl:if>

					<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.patent.patent-number"/>
						<xsl:with-param name="value" select="cerif:Import/cerif:PatentNumber"/>
				  </xsl:call-template>	

					<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.patent.approval-date"/>
						<xsl:with-param name="value" select="cerif:Import/cerif:ApprovalDate"/>
				  </xsl:call-template>	

					<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.patent.publication-date"/>
						<xsl:with-param name="value" select="cerif:Import/cerif:PublicationDate"/>
				  </xsl:call-template>	

					<xsl:call-template name="key-author-affilation-comma-list">
						<xsl:with-param name="key" select="$lang.patent.inventor"/>
						<xsl:with-param name="value" select="cerif:Contributor/cerif:Index"/>
				  </xsl:call-template>	

					<xsl:call-template name="key-value-comma-list"> 
						<xsl:with-param name="key" select="$lang.patent.issuer"/>
						<xsl:with-param name="value" select="cerif:Import/cerif:Issuer"/>
				  </xsl:call-template>	

					<xsl:call-template name="key-value-comma-list"> 
						<xsl:with-param name="key" select="$lang.patent.ipc-class"/>
						<xsl:with-param name="value" select="cerif:Import/cerif:IPCClass"/>
				  </xsl:call-template>	

					<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="concat($lang.language,' ',$lang.abstract)"/>
						<xsl:with-param name="value" select="cerif:Import/cerif:Language"/>
				  </xsl:call-template>	

					<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.abstract"/>
						<xsl:with-param name="value" select="cerif:Import/cerif:Abstract"/>
				  </xsl:call-template>	

				</fo:block> 		

			</xsl:for-each> 

	  </xsl:if>

	</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE SPINOFF ENTITY -->	
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
					<xsl:with-param name="fontSize" select="$font.size.key"/>
					<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
				</xsl:call-template>	

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.spinoff.description"/>
					<xsl:with-param name="value" select="cerif:Description"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.spinoff.result-registercourt"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:RegisterCourt"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.spinoff.result-registertype"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:RegisterType"/>
				</xsl:call-template>

								<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.spinoff.result-registernumber"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:RegisterNumber"/>
				</xsl:call-template>

								<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.spinoff.result-foundingdate"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:FoundingDate"/>
				</xsl:call-template>

								<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.spinoff.result-type"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:Type"/>
				</xsl:call-template>

								<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.spinoff.result-nace"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:NACE"/>
				</xsl:call-template>

								<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.spinoff.result-street"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:Street"/>
				</xsl:call-template>

								<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.spinoff.result-postcode"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:PostCode"/>
				</xsl:call-template>

								<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.spinoff.result-city"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:City"/>
				</xsl:call-template>

								<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.spinoff.result-country"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:Country"/>
				</xsl:call-template>

								<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.spinoff.result-webaddress"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:WebAddress"/>
				</xsl:call-template>

					<xsl:call-template name="key-value-line-list">				
							<xsl:with-param name="key" select="$lang.spinoff.result-relatedorganisation"/>
							<xsl:with-param name="value" select="cerif:Result/cerif:RelatedOrganisation"/>
					</xsl:call-template>

			<xsl:call-template name="title">
				<xsl:with-param name="title" select="$lang.spinoff.funding-sectiontitle"/>
				<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
			</xsl:call-template>


					<fo:table>
					
										
							<fo:table-column column-number="1" column-width="20%"/>	
							<fo:table-column column-number="2" column-width="20%"/>	
							<fo:table-column column-number="3" column-width="20%"/>	
							<fo:table-column column-number="4" column-width="20%"/>	
							<fo:table-column column-number="5" column-width="20%"/>	


					<!--		<fo:table-column column-number="1" column-width="4cm"/>
							<fo:table-column column-number="2" column-width="4cm"/>
							<fo:table-column column-number="3" column-width="4cm"/>
							<fo:table-column column-number="4" column-width="4cm"/>
							<fo:table-column column-number="5" column-width="4cm"/> -->
							

							<fo:table-header>
								<fo:table-row>
								<fo:table-cell border-width="{$width.border}"
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
											<xsl:value-of select="$lang.spinoff.funding-year"/>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell border-width="{$width.border}"
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
											<xsl:value-of select="$lang.spinoff.funding-title"/>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell border-width="{$width.border}"
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
											<xsl:value-of select="$lang.spinoff.funding-amount"/>
									</fo:block>
								</fo:table-cell>

									<fo:table-cell border-width="{$width.border}"
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
											<xsl:value-of select="$lang.currency"/>
									</fo:block>
								</fo:table-cell>
							
									<fo:table-cell border-width="{$width.border}"
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
											<xsl:value-of select="$lang.spinoff.funding-description"/>
									</fo:block>
								</fo:table-cell>
							
								</fo:table-row>
							</fo:table-header>

							<fo:table-body>
								<xsl:for-each select="cerif:Funding/cerif:Index">
								<fo:table-row>
									<fo:table-cell border-width="{$width.border}"
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

									<fo:table-cell border-width="{$width.border}"
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

									<fo:table-cell border-width="{$width.border}"
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

									<fo:table-cell border-width="{$width.border}"
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

									<fo:table-cell border-width="{$width.border}"
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
				<xsl:with-param name="title" select="$lang.spinoff.performance-title"/>
				<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
			</xsl:call-template>


					<fo:table>
							<fo:table-column column-number="1" column-width="33,3%"/>
							<fo:table-column column-number="2" column-width="33,3%"/>
							<fo:table-column column-number="3" column-width="33,3%"/>

							
							<fo:table-header>
								<fo:table-row>
								<fo:table-cell border-width="{$width.border}"
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
											<xsl:value-of select="$lang.spinoff.performance-year"/>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell border-width="{$width.border}"
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
											<xsl:value-of select="$lang.spinoff.performance-amount"/>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell border-width="{$width.border}"
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
											<xsl:value-of select="$lang.currency"/>
									</fo:block>
								</fo:table-cell>		
								</fo:table-row>
							</fo:table-header>

							<fo:table-body>
								<xsl:for-each select="cerif:Performance/cerif:Index">
								<fo:table-row>
									<fo:table-cell border-width="{$width.border}"
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

									<fo:table-cell border-width="{$width.border}"
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

									<fo:table-cell border-width="{$width.border}"
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
<!-- TEMPLATE FOR THE WORK AND COOPERATION AFTER PROJEKT END -->	
<!--########################################################################-->

	<xsl:template name="furthersteps">

		<xsl:if test="cerif:Step">

			<xsl:for-each select="cerif:Step/cerif:Index">

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
						<xsl:with-param name="fontSize" select="$font.size.key"/>
						<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
					</xsl:call-template>

					<xsl:call-template name="key-value-line-list">
						<xsl:with-param name="key" select="$lang.step.partner"/>
						<xsl:with-param name="value" select="cerif:Partner"/>
				  </xsl:call-template>

					<xsl:call-template name="key-value-line-list">
						<xsl:with-param name="key" select="$lang.step.targetgroup"/>
						<xsl:with-param name="value" select="cerif:TargetGroup"/>
				  </xsl:call-template>

					<xsl:call-template name="key-value-line-list">
						<xsl:with-param name="key" select="$lang.step.type"/>
						<xsl:with-param name="value" select="cerif:Type"/>
				  </xsl:call-template>

					<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.step.date"/>
						<xsl:with-param name="value" select="cerif:Date"/>
				  </xsl:call-template>

					<xsl:call-template name="key-value-single">
						<xsl:with-param name="key" select="$lang.step.description"/>
						<xsl:with-param name="value" select="cerif:Description"/>
				  </xsl:call-template>

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
					<xsl:with-param name="fontSize" select="$font.size.key"/>
					<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
				</xsl:call-template>	

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.award.description"/>
					<xsl:with-param name="value" select="cerif:Description"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.award.date"/>
					<xsl:with-param name="value" select="cerif:Date"/>
				</xsl:call-template>

				<xsl:call-template name="key-amount-currency">
					<xsl:with-param name="key" select="$lang.award.pricemoney"/>
					<xsl:with-param name="amount" select="cerif:PriceMoney"/>
					<xsl:with-param name="currency" select="cerif:Currency"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.currency"/>
					<xsl:with-param name="value" select="cerif:Currency"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-comma-list">
					<xsl:with-param name="key" select="$lang.award.winner"/>
					<xsl:with-param name="value" select="cerif:Winner"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-comma-list">
					<xsl:with-param name="key" select="$lang.award.organisation"/>
					<xsl:with-param name="value" select="cerif:Organisation"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-comma-list">
					<xsl:with-param name="key" select="$lang.award.donor"/>
					<xsl:with-param name="value" select="cerif:Donor"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.award.targetgroup"/>
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

	<xsl:call-template name="title-description">
		<xsl:with-param name="entity" select="cerif:OpenResearchQuestion"/>
		<xsl:with-param name="entityIndex" select="cerif:OpenResearchQuestion/cerif:Index"/>
	</xsl:call-template>	

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
					<xsl:with-param name="fontSize" select="$font.size.key"/>
					<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
				</xsl:call-template>	

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.condition.conditiontype"/>
					<xsl:with-param name="value" select="cerif:ConditionType"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.condition.influence"/>
					<xsl:with-param name="value" select="cerif:Influence"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.condition.startframework"/>
					<xsl:with-param name="value" select="cerif:StartFramework"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.condition.conditiondescription"/>
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
					<xsl:with-param name="fontSize" select="$font.size.key"/>
					<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
				</xsl:call-template>	

							<xsl:call-template name="key-value-single">
				<xsl:with-param name="key" select="$lang.expectedsocietalimpact.description"/>
				<xsl:with-param name="value" select="cerif:Description"/>
			</xsl:call-template>

				<xsl:call-template name="key-value-single">
				<xsl:with-param name="key" select="$lang.expectedsocietalimpact.transitionarea"/>
				<xsl:with-param name="value" select="cerif:TransitionArea"/>
			</xsl:call-template>

			<xsl:call-template name="key-value-single">
				<xsl:with-param name="key" select="$lang.expectedsocietalimpact.evidence"/>
				<xsl:with-param name="value" select="cerif:Evidence"/>
			</xsl:call-template>

			<xsl:call-template name="title">
				<xsl:with-param name="title" select="$lang.expectedsocietalimpact.type"/>
				<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
			</xsl:call-template>

					<fo:table>
							<fo:table-column column-number="1" column-width="6cm"/>
							<fo:table-column column-number="2" column-width="6cm"/>
							<fo:table-column column-number="3" column-width="8cm"/>
							
							<fo:table-header>
								<fo:table-row>
								<fo:table-cell border-width="{$width.border}"
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
											<xsl:value-of select="$lang.expectedsocietalimpact.type-coreelement"/>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell border-width="{$width.border}"
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
											<xsl:value-of select="$lang.expectedsocietalimpact.type-intensity"/>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell border-width="{$width.border}"
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
											<xsl:value-of select="$lang.expectedsocietalimpact.type-description"/>
									</fo:block>
								</fo:table-cell>
								</fo:table-row>
							</fo:table-header>

							<fo:table-body>
								<xsl:for-each select="cerif:Type/cerif:Index">
								<fo:table-row>
									<fo:table-cell border-width="{$width.border}"
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

									<fo:table-cell border-width="{$width.border}"
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

									<fo:table-cell border-width="{$width.border}"
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
					<xsl:with-param name="fontSize" select="$font.size.key"/>
					<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
				</xsl:call-template>	

				<xsl:call-template name="key-value-single">
				<xsl:with-param name="key" select="$lang.negativesideeffect.description"/>
				<xsl:with-param name="value" select="cerif:Description"/>
			</xsl:call-template>

				<xsl:call-template name="key-value-single">
				<xsl:with-param name="key" select="$lang.negativesideeffect.transitionArea"/>
				<xsl:with-param name="value" select="cerif:TransitionArea"/>
			</xsl:call-template>

			<xsl:call-template name="key-value-single">
				<xsl:with-param name="key" select="$lang.negativesideeffect.evidence"/>
				<xsl:with-param name="value" select="cerif:Evidence"/>
			</xsl:call-template>

			<xsl:call-template name="title">
				<xsl:with-param name="title" select="$lang.negativesideeffect.type"/>
				<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
			</xsl:call-template>

					<fo:table>
							<fo:table-column column-number="1" column-width="30%"/>
							<fo:table-column column-number="2" column-width="20%"/>
							<fo:table-column column-number="3" column-width="50%"/>
							
							<fo:table-header>
								<fo:table-row>
								<fo:table-cell border-width="{$width.border}"
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
											<xsl:value-of select="$lang.negativesideeffect.type-coreelement"/>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell border-width="{$width.border}"
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
											<xsl:value-of select="$lang.negativesideeffect.type-intensity"/>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell border-width="{$width.border}"
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
											<xsl:value-of select="$lang.negativesideeffect.type-description"/>
									</fo:block>
								</fo:table-cell>
								</fo:table-row>
							</fo:table-header>

							<fo:table-body>
							 	<xsl:for-each select="cerif:Type/cerif:Index">
								<fo:table-row>
									<fo:table-cell border-width="{$width.border}"
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

									<fo:table-cell border-width="{$width.border}"
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

									<fo:table-cell border-width="{$width.border}"
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
					<xsl:with-param name="fontSize" select="$font.size.key"/>
					<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
				</xsl:call-template>

			<xsl:call-template name="title">
				<xsl:with-param name="title" select="$lang.ethic.keymessage"/>
				<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
			</xsl:call-template>

					<fo:table>
							<fo:table-column column-number="1" column-width="8cm"/>
							<fo:table-column column-number="2" column-width="12cm"/>
													
							<fo:table-header>
								<fo:table-row>
								<fo:table-cell border-width="{$width.border}"
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
											<xsl:value-of select="$lang.ethic.reflection-aspecttype"/>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell border-width="{$width.border}"
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
											<xsl:value-of select="$lang.ethic.reflection-ethicdescription"/>
									</fo:block>
								</fo:table-cell>
								</fo:table-row>
							</fo:table-header>

							<fo:table-body>
								<xsl:for-each select="cerif:Reflection/cerif:Index">
								<fo:table-row>
									<fo:table-cell border-width="{$width.border}"
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

									<fo:table-cell border-width="{$width.border}"
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
				<xsl:with-param name="key" select="$lang.ethic.furtherdocuments"/>
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
					<xsl:with-param name="fontSize" select="$font.size.key"/>
					<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
				</xsl:call-template>	

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.gender.reflection-researchquestion"/>
					<xsl:with-param name="value" select="cerif:Reflection/cerif:Researchquestion"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.gender.reflection-descriptionresearchquestion"/>
					<xsl:with-param name="value" select="cerif:Reflection/cerif:DescriptionResearchquestion"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.gender.reflection-stateofresearch"/>
					<xsl:with-param name="value" select="cerif:Reflection/cerif:StateOfResearch"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.gender.reflection-descriptionstateofresearch"/>
					<xsl:with-param name="value" select="cerif:Reflection/cerif:DescriptionStateOfResearch"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.gender.reflection-relevance"/>
					<xsl:with-param name="value" select="cerif:Reflection/cerif:Relevance"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.gender.reflection-descriptionrelevance"/>
					<xsl:with-param name="value" select="cerif:Reflection/cerif:DescriptionRelevance"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.gender.reflection-application"/>
					<xsl:with-param name="value" select="cerif:Reflection/cerif:Application"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.gender.reflection-descriptionapplication"/>
					<xsl:with-param name="value" select="cerif:Reflection/cerif:DescriptionApplication"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.gender.reflection-implementation"/>
					<xsl:with-param name="value" select="cerif:Reflection/cerif:Implementation"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.gender.reflection-descriptionimplementaiton"/>
					<xsl:with-param name="value" select="cerif:Reflection/cerif:DescriptionImplementaiton"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.gender.reflection-genderbalance"/>
					<xsl:with-param name="value" select="cerif:Reflection/cerif:GenderBalance"/>
				</xsl:call-template>

				<xsl:call-template name="key-value-single">
					<xsl:with-param name="key" select="$lang.gender.reflection-descriptiongenderbalance"/>
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
						<xsl:with-param name="fontSize" select="$font.size.key"/>
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
						<xsl:with-param name="fontSize" select="$font.size.key"/>
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
<!-- TEMPLATE FOR THE WORK PACKAGES - TASK TEST -->	
<!--########################################################################-->

<!-- INFO: This was only used a proof of concept for the work package - task aggregation -->

	<xsl:template name="work-package-task-test">
				
	  <xsl:if test="cerif:WorkPackage">

		  <fo:block>

			  <xsl:for-each select="cerif:WorkPackage/cerif:Index">	

					<xsl:call-template name="title">
						<xsl:with-param name="title" select="cerif:Title"/>
						<xsl:with-param name="fontSize" select="$font.size.title"/>
					</xsl:call-template>

				  <xsl:call-template name="key-value-line-list">
						<xsl:with-param name="key" select="'Items'"/>
						<xsl:with-param name="value" select="cerif:Task/cerif:Index/cerif:Title"/>
				  </xsl:call-template>	

				</xsl:for-each>
			
			</fo:block>

	  </xsl:if>

  </xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE TARGET GROUPS - EVENT TEST -->	
<!--########################################################################-->

<!-- INFO: This was only used a proof of concept for the target group - event aggregation -->

	<xsl:template name="target-group-event-test">
				
	  <xsl:if test="cerif:TargetGroup">

		  <fo:block>

			  <xsl:for-each select="cerif:TargetGroup/cerif:Index">	

					<xsl:call-template name="title">
						<xsl:with-param name="title" select="cerif:Title"/>
						<xsl:with-param name="fontSize" select="$font.size.title"/>
					</xsl:call-template>

				  <xsl:call-template name="key-value-line-list">
						<xsl:with-param name="key" select="'Items'"/>
						<xsl:with-param name="value" select="cerif:Event/cerif:Index/cerif:Title"/>
				  </xsl:call-template>	

				</xsl:for-each>
			
			</fo:block>

	  </xsl:if>

  </xsl:template>

<!--########################################################################-->
<!-- GENERAL TEMPLATES -->	
<!--########################################################################-->

<!-- TODO: check which templates are not used -->

<!--==============================================================-->
<!-- vertical gap -->	
<!--==============================================================-->

	<xsl:template name="vertical-gap">
		<xsl:param name="margin"/>

		<fo:block margin-left="{$margin.left.text}"
							margin-top="{$margin}">
		</fo:block>

	</xsl:template>

<!--==============================================================-->	
<!-- title -->	
<!--==============================================================-->

	<xsl:template name="title">
		<xsl:param name="title"/>
		<xsl:param name="fontSize" select="$font.size.title"/>

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
<!-- single value -->
<!--==============================================================-->

	<xsl:template name="value-single">
	  <xsl:param name="value"/>
		<xsl:param name="fontSize" select="$font.size.standard"/>
		<xsl:param name="fontWeight" select="$font.weight.value"/>
    <xsl:param name="addRuler" select="'true'"/>

    <!-- content -->
	  <xsl:if test="$value">
		  <fo:block margin-left="{$margin.left.text}"
								font-size="{$fontSize}" 
			        	margin-top="{$margin.top.main}">

				<fo:inline font-weight="{$fontWeight}" 
										text-align="{$text.alignment.value}">
					<xsl:value-of select="$value"/> 
				</fo:inline>

			</fo:block>

			<xsl:if test="$addRuler = 'true'">
				<xsl:call-template name="ruler"/>
			</xsl:if>

	  </xsl:if>
	</xsl:template>

<!--==============================================================-->	
<!-- value list separated by commas -->
<!--==============================================================-->

	<xsl:template name="value-comma-list">
	  <xsl:param name="value"/>
		<xsl:param name="fontSize" select="$font.size.standard"/>
		<xsl:param name="fontWeight" select="$font.weight.value"/>

    <!-- content -->
	  <xsl:if test="$value">
		  <fo:block margin-left="{$margin.left.text}"
								font-size="{$fontSize}" 
			        	margin-top="{$margin.top.main}">

				<fo:inline font-weight="{$fontWeight}" 
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
<!-- value set with postcode, city and and country -->
<!--==============================================================-->

	<xsl:template name="value-postcode-city-country">
	  <xsl:param name="postcode"/>
	  <xsl:param name="city"/>
		<xsl:param name="country"/>

	  <xsl:if test="$city"> <!--  use only city for check, this seems the most probable input -->
			<fo:block margin-left="{$margin.left.text}"
								font-size="{$font.size.standard}" 
								margin-top="{$margin.top.main}">

				<xsl:if test="$postcode"> 
					<xsl:text> </xsl:text>
					<fo:inline font-weight="{$font.weight.value}" 
											text-align="{$text.alignment.value}">
						<xsl:value-of select="$postcode"/> 
					</fo:inline>

				  <xsl:text> </xsl:text>	
			  </xsl:if>

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
<!-- value set with name, degree and gender -->
<!--==============================================================-->

	<xsl:template name="value-name-degree-gender">
	  <xsl:param name="name"/>
	  <xsl:param name="degree"/>
		<xsl:param name="gender"/>

	  <xsl:if test="$name"> <!--  use only name for check which is the most important input -->
			<fo:block margin-left="{$margin.left.text}"
								font-size="{$font.size.standard}" 
								margin-top="{$margin.top.main}">

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
					<xsl:text> (</xsl:text>
					<fo:inline font-weight="{$font.weight.value}" 
											text-align="{$text.alignment.value}">
						<xsl:value-of select="$gender"/> 
					</fo:inline>
					<xsl:text>)</xsl:text>
			  </xsl:if>

			</fo:block>

	  </xsl:if>
	</xsl:template>

<!--==============================================================-->	
<!-- key: no value -->
<!--==============================================================-->

	<xsl:template name="key-only">
	  <xsl:param name="key"/>

	  <xsl:if test="$key">
		  <fo:block margin-left="{$margin.left.text}"
								font-size="{$font.size.standard}" 
			        	margin-top="{$margin.top.main}"
								color="{$colour.key}">

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

	<xsl:template name="key-value-single">
	  <xsl:param name="key"/>
	  <xsl:param name="value"/>
		<xsl:param name="addRuler" select="'true'"/>

	  <xsl:if test="$value">
		  <fo:block margin-left="{$margin.left.text}"
								font-size="{$font.size.standard}" 
			        	margin-top="{$margin.top.main}">

				<fo:inline font-weight="{$font.weight.key}" 
				           text-align="{$text.alignment.key}"
									 color="{$colour.key}">
					<xsl:value-of select="$key"/>

					<xsl:text>: </xsl:text> 
				</fo:inline>

				<fo:inline font-weight="{$font.weight.value}" 
									 text-align="{$text.alignment.value}">
					<xsl:value-of select="$value"/> 
				</fo:inline>

			</fo:block>

      <xsl:if test="$addRuler = 'true'">
				<xsl:call-template name="ruler"/>
			</xsl:if>

	  </xsl:if>
	</xsl:template>

<!--==============================================================-->	
<!-- key: single value below key-->
<!--==============================================================-->

<xsl:template name="key-value-single-below">
  <xsl:param name="key"/>
  <xsl:param name="value"/>
	<xsl:param name="checkValue" select="'true'"/>
	<xsl:param name="addRuler" select="'true'"/>

  <xsl:choose>
	  <xsl:when test="$checkValue = 'true'">

    <!-- print only anything when a value is given -->
	    <xsl:if test="$value">

				<fo:block margin-left="{$margin.left.text}"
									font-size="{$font.size.key.alternative}" 
									margin-top="{$margin.top.main}">

					<fo:inline font-weight="{$font.weight.key}" 
										 text-align="{$text.alignment.key}"
										 color="{$colour.key}">
						<xsl:value-of select="$key"/>
					</fo:inline>

				</fo:block>

				<fo:block margin-left="{$margin.left.text}"
									font-size="{$font.size.standard}" 
									margin-top="{$margin.top.main}">

					<fo:inline font-weight="{$font.weight.value}" 
										text-align="{$text.alignment.value}">
						<xsl:value-of select="$value"/> 
					</fo:inline>

				</fo:block>

				<xsl:if test="$addRuler = 'true'">
					<xsl:call-template name="ruler"/>
				</xsl:if>

      </xsl:if>

		</xsl:when>
	  <xsl:otherwise>

    <!-- do not check if a value exists, simply print everything -->
			<fo:block margin-left="{$margin.left.text}"
								font-size="{$font.size.key.alternative}" 
								margin-top="{$margin.top.main}">

				<fo:inline font-weight="{$font.weight.key}" 
									 text-align="{$text.alignment.key}"
									 color="{$colour.key}">
					<xsl:value-of select="$key"/>
				</fo:inline>
			</fo:block>

			<fo:block margin-left="{$margin.left.text}"
								font-size="{$font.size.standard}" 
								margin-top="0">
				<fo:inline font-weight="{$font.weight.value}" 
									 text-align="{$text.alignment.value}">
					<xsl:value-of select="$value"/> 
				</fo:inline>
			</fo:block>

			<xsl:if test="$addRuler = 'true'">
				<xsl:call-template name="ruler"/>
			</xsl:if>

		</xsl:otherwise>
	</xsl:choose>

</xsl:template>

<!--==============================================================-->	
<!-- key: period -->
<!--==============================================================-->

	<xsl:template name="key-period">
	  <xsl:param name="key"/>
	  <xsl:param name="startdate"/>
		<xsl:param name="enddate"/>
		<xsl:param name="addRuler" select="'true'"/>

	  <xsl:if test="$startdate and $enddate">

			<fo:block margin-left="{$margin.left.text}"
								font-size="{$font.size.standard}" 
								margin-top="{$margin.top.main}">

				<fo:inline font-weight="{$font.weight.key}" 
										text-align="{$text.alignment.key}"
										color="{$colour.key}">
					<xsl:value-of select="$key"/> 

					<xsl:text>: </xsl:text>
				</fo:inline>

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

			<xsl:if test="$addRuler = 'true'">
				<xsl:call-template name="ruler"/>
			</xsl:if>

	  </xsl:if>
	</xsl:template>

<!--==============================================================-->	
<!-- key: postcode city / country -->
<!--==============================================================-->

	<xsl:template name="key-postcode-city-country">
		<xsl:param name="key"/>
	  <xsl:param name="postcode"/>
	  <xsl:param name="city"/>
		<xsl:param name="country"/>
		<xsl:param name="addRuler" select="'true'"/>

	  <xsl:if test="$city"> <!--  use only city for check, this seems the most probable input -->
			<fo:block margin-left="{$margin.left.text}"
								font-size="{$font.size.standard}" 
								margin-top="{$margin.top.main}">

				<fo:inline font-weight="{$font.weight.key}" 
					  				 text-align="{$text.alignment.key}"
										 color="{$colour.key}">
					<xsl:value-of select="$key"/> 

				  <xsl:text>: </xsl:text>
				</fo:inline>

				<xsl:if test="$postcode"> 
					<xsl:text> </xsl:text>
					<fo:inline font-weight="{$font.weight.value}" 
										 text-align="{$text.alignment.value}">
						<xsl:value-of select="$postcode"/> 
					</fo:inline>

					<xsl:text> </xsl:text>
			  </xsl:if>

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

			<xsl:if test="$addRuler = 'true'">
				<xsl:call-template name="ruler"/>
			</xsl:if>

	  </xsl:if>
	</xsl:template>

<!--==============================================================-->	
<!-- key: amount and currency -->
<!--==============================================================-->

	<xsl:template name="key-amount-currency">
		<xsl:param name="key"/>
	  <xsl:param name="amount"/>
	  <xsl:param name="currency" select="'Euro'"/>
		<xsl:param name="addRuler" select="'true'"/>

	  <xsl:if test="$amount"> <!--  use only amount for check, this seems the most probable input -->
			<fo:block margin-left="{$margin.left.text}"
								font-size="{$font.size.standard}" 
								margin-top="{$margin.top.main}">

				<fo:inline font-weight="{$font.weight.key}" 
					  				 text-align="{$text.alignment.key}"
										 color="{$colour.key}">
					<xsl:value-of select="$key"/> 

					<xsl:text>: </xsl:text>
				</fo:inline>

				<fo:inline font-weight="{$font.weight.value}" 
										text-align="{$text.alignment.value}">
					<xsl:value-of select="$amount"/> 
				</fo:inline>

				<xsl:text> </xsl:text>

				<fo:inline font-weight="{$font.weight.value}" 
										text-align="{$text.alignment.value}">
					<xsl:value-of select="$currency"/> 
				</fo:inline>

			</fo:block>

			<xsl:if test="$addRuler = 'true'">
				<xsl:call-template name="ruler"/>
			</xsl:if>

	  </xsl:if>
	</xsl:template>

<!--==============================================================-->	
<!-- key: name degree (gender) -->
<!--==============================================================-->

	<xsl:template name="key-name-degree-gender">
		<xsl:param name="key"/>
	  <xsl:param name="name"/>
	  <xsl:param name="degree"/>
		<xsl:param name="gender"/>
		<xsl:param name="addRuler" select="'true'"/>

	  <xsl:if test="$name"> <!--  use only name for check which is the most important input -->
			<fo:block margin-left="{$margin.left.text}"
								font-size="{$font.size.standard}" 
								margin-top="{$margin.top.main}">

				<fo:inline font-weight="{$font.weight.key}" 
										 text-align="{$text.alignment.key}"
										 color="{$colour.key}">
					<xsl:value-of select="$key"/> 

					<xsl:text>: </xsl:text>
				</fo:inline>

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
					<xsl:text> (</xsl:text>
					<fo:inline font-weight="{$font.weight.value}" 
											text-align="{$text.alignment.value}">
						<xsl:value-of select="$gender"/> 
					</fo:inline>
					<xsl:text>)</xsl:text>
			  </xsl:if>

			</fo:block>

			<xsl:if test="$addRuler = 'true'">
				<xsl:call-template name="ruler"/>
			</xsl:if>

	  </xsl:if>
	</xsl:template>

<!--==============================================================-->	
<!-- key: value list separated by commas -->
<!--==============================================================-->

	<xsl:template name="key-value-comma-list">
		<xsl:param name="key"/>
	  <xsl:param name="value"/>
		<xsl:param name="addRuler" select="'true'"/>

	  <xsl:if test="$value">

		  <fo:block margin-left="{$margin.left.text}" 
								font-size="{$font.size.standard}" 
			          margin-top="{$margin.top.main}">

				<fo:inline font-weight="{$font.weight.key}" 
									 text-align="{$text.alignment.key}"
									 color="{$colour.key}">
					<xsl:value-of select="$key"/> 

					<xsl:text>: </xsl:text>
				</fo:inline>

				<fo:inline font-weight="{$font.weight.value}" 
									 text-align="{$text.alignment.value}">
					<xsl:for-each select="$value">
					  <xsl:value-of select="current()"/>
					  <xsl:if test="position() != last()">, </xsl:if> <!-- do only this for the last item -->
					</xsl:for-each>
				</fo:inline>

			</fo:block>

			<xsl:if test="$addRuler = 'true'">
				<xsl:call-template name="ruler"/>
			</xsl:if>

		</xsl:if>
	</xsl:template>	

<!--==============================================================-->	
<!-- key: every value in a separated line -->
<!--==============================================================-->

	<xsl:template name="key-value-line-list">
		<xsl:param name="key"/>
	  <xsl:param name="value"/>
		<xsl:param name="addRuler" select="'true'"/>

		<xsl:if test="$value"> <!-- do only if there are values -->

			<fo:block margin-left="{$margin.left.text}"
								font-size="{$font.size.standard}" 
			          margin-top="{$margin.top.main}"
								color="{$colour.key}">

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

			<xsl:if test="$addRuler = 'true'">
				<xsl:call-template name="ruler"/>
			</xsl:if>

	  </xsl:if>

  </xsl:template>	

<!--==============================================================-->
<!-- key: author (affiliation) separated by commas -->
<!--==============================================================-->

	<xsl:template name="key-author-affilation-comma-list">
		<xsl:param name="key"/>
	  <xsl:param name="value"/>
		<xsl:param name="addRuler" select="'true'"/>

	  <xsl:if test="$value">

		  <fo:block margin-left="{$margin.left.text}" 
								font-size="{$font.size.standard}" 
			          margin-top="{$margin.top.main}">

				<fo:inline font-weight="{$font.weight.key}" 
									 text-align="{$text.alignment.key}"
									 color="{$colour.key}">
					<xsl:value-of select="$key"/>

					<xsl:text>: </xsl:text>	 
				</fo:inline>

				<fo:inline font-weight="{$font.weight.value}" 
									 text-align="{$text.alignment.value}">

					<xsl:for-each select="$value">

					  <xsl:value-of select="cerif:Author"/>

					    <xsl:if test="cerif:Affiliation">
						  <xsl:text> (</xsl:text>
              <xsl:value-of select="cerif:Affiliation"/> 
							<xsl:text>)</xsl:text>
							</xsl:if>

					  <xsl:if test="position() != last()">
						  <xsl:text>, </xsl:text>
						</xsl:if>

					</xsl:for-each>

				</fo:inline>
			</fo:block>

			<xsl:if test="$addRuler = 'true'">
				<xsl:call-template name="ruler"/>
			</xsl:if>

		</xsl:if>

  </xsl:template>	

<!--==============================================================-->
<!-- block with title (first row) and description (second row) -->
<!--==============================================================-->

	<xsl:template name="title-description">
		<xsl:param name="entity"/>
		<xsl:param name="entityIndex"/>
	
		<xsl:if test="$entity">
	
			<xsl:for-each select="$entityIndex">
	
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

        <!-- title -->
					<xsl:call-template name="value-single">
						<xsl:with-param name="value" select="cerif:Title"/>
						<xsl:with-param name="fontSize" select="$font.size.key"/>
						<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
						<xsl:with-param name="addRuler" select="'false'"/>
					</xsl:call-template>
 
        <!-- description -->
					<xsl:if test="cerif:Description">							
            <xsl:call-template name="ruler"/>

						<xsl:call-template name="value-single">
							<xsl:with-param name="value" select="cerif:Description"/>
							<xsl:with-param name="addRuler" select="'false'"/>
						</xsl:call-template>

					</xsl:if>
          
        </fo:block>

		  </xsl:for-each> 
	
		</xsl:if>
	
	</xsl:template>

<!--==============================================================-->
<!-- ruler -->
<!--==============================================================-->

	<xsl:template name="ruler" >

		<fo:block text-align="center">
			<fo:leader leader-pattern="rule" 
								 leader-length="{$width.ruler}" 
								 rule-style="{$style.ruler}" 
								 rule-thickness="{$width.border}" 
								 color="{$colour.border}"/>
		</fo:block>

	</xsl:template>

</xsl:stylesheet>