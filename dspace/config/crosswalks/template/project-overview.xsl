<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1"
								xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
								xmlns:fo="http://www.w3.org/1999/XSL/Format"
								xmlns:cerif="https://www.openaire.eu/cerif-profile/1.1/"
								xmlns:fox="http://xmlgraphics.apache.org/fop/extensions"
								xmlns:pdf="http://xmlgraphics.apache.org/fop/extensions/pdf"
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

<xsl:param name="lang.title.section2" select="'2 Hintergrund'"/>
<xsl:param name="lang.title.section2.1" select="'2.1 Stand der Wissenschaft und Technik / Bisherige Arbeiten'"/>

<xsl:param name="lang.title.section3" select="'3 Ausführliche Beschreibung des Arbeitsplans'"/>
<xsl:param name="lang.title.section3.1" select="'3.1 Vorhabenbezogene Ressourcenplanung'"/>
<xsl:param name="lang.title.section3.1.1" select="'3.1.1 Überblick Arbeitsplanung'"/>
<xsl:param name="lang.title.section3.1.2" select="'3.1.2 Arbeitspakete und Arbeiten'"/>
<xsl:param name="lang.title.section3.1.3" select="'3.1.3 Forschungsarbeiten'"/>
<xsl:param name="lang.title.section3.1.4" select="'3.1.4 Veröffentlichungen'"/>
<xsl:param name="lang.title.section3.1.5" select="'3.1.5 Forschungdaten'"/>
<xsl:param name="lang.title.section3.1.6" select="'3.1.6 Arbeiten zur Interaktion und Transfer'"/>
<xsl:param name="lang.title.section3.2" select="'3.2 Meilensteinplan'"/>
<xsl:param name="lang.title.section3.3" select="'3.3 Vertiefung Material und Methoden'"/>

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

<xsl:param name="lang.title.section5" select="'5 Arbeitsteilung/Zusammenarbeit mit Dritten'"/>
<xsl:param name="lang.title.section5.1" select="'5.1 Überblick'"/>
<xsl:param name="lang.title.section5.2" select="'5.2 Kooperationspartner (zusätzliche Informationen)'"/>
<xsl:param name="lang.title.section5.3" select="'5.3 Relevante Akteursgruppen des Projektes (zusätzliche Informationen)'"/>

<xsl:param name="lang.title.section6" select="'6 Notwendigkeit der Zuwendung'"/>

<!-- individual fields -->
<xsl:param name="lang.title" select="'Titel'"/>
<xsl:param name="lang.description" select="'Beschreibung'"/>
<xsl:param name="lang.abstract" select="'Zusammenfassung'"/>
<xsl:param name="lang.type" select="'Art'"/>
<xsl:param name="lang.language" select="'Sprache'"/>
<xsl:param name="lang.identifier" select="'Identifier'"/>
<xsl:param name="lang.page" select="'Seite '"/>
<xsl:param name="lang.term" select="'Laufzeit'"/>
<xsl:param name="lang.period" select="'Zeitraum'"/>
<xsl:param name="lang.date" select="'Datum'"/>
<xsl:param name="lang.hour.singular" select="'Stunde'"/>
<xsl:param name="lang.hour.plural" select="'Stunden'"/>
<xsl:param name="lang.month.singular" select="'Monat'"/>
<xsl:param name="lang.month.plural" select="'Monate'"/>
<xsl:param name="lang.year" select="'Jahr'"/>
<xsl:param name="lang.postcode-city-country" select="'PLZ / Ort / Land'"/>
<xsl:param name="lang.postcode" select="'PLZ'"/>
<xsl:param name="lang.city" select="'Ort'"/>
<xsl:param name="lang.country" select="'Land'"/>
<xsl:param name="lang.street" select="'Straße, Hausnummer'"/>
<xsl:param name="lang.web-address" select="'Webadresse'"/>
<xsl:param name="lang.currency" select="'Währung'"/>
<xsl:param name="lang.author" select="'Autor:in(nen)'"/>
<xsl:param name="lang.unit" select="'Einheit'"/>
<xsl:param name="lang.keyword" select="'Schlagworte'"/>


<xsl:param name="lang.organisation.form" select="'Organisationsform'"/>
<xsl:param name="lang.cooperation.type" select="'Art der Kooperation'"/>

<xsl:param name="lang.funding.call" select="'Förderbereich/Bekanntmachung'"/>
<xsl:param name="lang.funding.programme" select="'Fördermaßnahme/Förderprogramm'"/>

<xsl:param name="lang.project-partner.easy-online" select="'Ausführende Stelle im easy-online-Antrag'"/>
<xsl:param name="lang.project-partner.field-of-activity" select="'Tätigkeitsbereich der Organisationseinheit'"/>
<xsl:param name="lang.project-partner.destatis" select="'Disziplin / Fachgebiet der Forschungseinrichtung'"/>
<xsl:param name="lang.project-partner.nace" select="'Wirtschaftszweig der Organisation'"/>
<xsl:param name="lang.project-partner.political-level" select="'Politikebene(n) der Organisationseinheit'"/>

<xsl:param name="lang.organisation.field-of-activity" select="'Tätigkeitsbereich der Organisation'"/>
<xsl:param name="lang.organisation.nace" select="'Wirtschaftszweig der Organisation'"/>
<xsl:param name="lang.organisation.destatis" select="'Disziplin / Fachgebiet der Organisation'"/>
<xsl:param name="lang.organisation.political-level" select="'Politikebenen der Organisation'"/>
<xsl:param name="lang.organisation.project-contribution" select="'Beschreibung der Zusammenarbeit'"/>			
<xsl:param name="lang.organisation.size" select="'Unternehmensgröße'"/>
<xsl:param name="lang.organisation.location-type" select="'Verortung der Akteursgruppe'"/>			

<xsl:param name="lang.target-group.relevance" select="'Relevanz der Akteursgruppe für das Vorhaben'"/>			

<xsl:param name="lang.work-package.requirement" select="'Voraussetzungen'"/>
<xsl:param name="lang.work-package.responsible-organisation" select="'Hauptverantwortlicher Projektpartner'"/>

<xsl:param name="lang.research-work.task" select="'Aufgaben'"/>
<xsl:param name="lang.research-work.event" select="'Events'"/>
<xsl:param name="lang.research-work.process-event" select="'Kooperationsprozesse'"/>
<xsl:param name="lang.research-work.planned-publication" select="'Geplante Veröffentlichungen'"/>
<xsl:param name="lang.research-work.physical-object" select="'Objekte/Materialien'"/>

<xsl:param name="lang.event.project-contribution" select="'Art der Beteiligung des Vorhabens'"/>
<xsl:param name="lang.event.purpose" select="'Veranstaltungszweck'"/>
<xsl:param name="lang.event.involved-organisation" select="'Beteiligte Projektpartner oder Unterauftragnehmer/ Kooperationspartner'"/>
<xsl:param name="lang.event.target-group" select="'Beteiligte Akteursgruppe(n)'"/>
<xsl:param name="lang.event.participant-number" select="'Anzahl Teilnehmende'"/>

<xsl:param name="lang.process-event.purpose" select="'Veranstaltungszweck'"/>
<xsl:param name="lang.process-event.involved-organisation" select="'Beteiligte Projektpartner oder Unterauftragnehmer/ Kooperationspartner'"/>
<xsl:param name="lang.process-event.target-group" select="'Beteiligte Akteursgruppe(n)'"/>





<xsl:param name="lang.physical-object.function" select="'Verwendungsmöglichkeiten'"/>
<xsl:param name="lang.physical-object.accessibility" select="'Öffentliche Zugänglichkeit'"/>
<xsl:param name="lang.physical-object.contact-information" select="'Kontaktinformationen für Anfragen'"/>

<xsl:param name="lang.planned-publication.target-audience" select="'Zielpublikum'"/>

<xsl:param name="lang.product.publication-date" select="'Datum der Veröffentlichung'"/>
<xsl:param name="lang.product.repository" select="'Repositorien'"/>
<xsl:param name="lang.product.reference" select="'Zugehörige Veröffentlichung(en)'"/>

<xsl:param name="lang.innovation-potential.target-group" select="'Nutzende Akteursgruppe(n)'"/>
<xsl:param name="lang.innovation-potential.description" select="'Beschreibung (Nutzen, Auswirkung)'"/>
<xsl:param name="lang.innovation-potential.applicability-novelty" select="'Innovationsgrad'"/>
<xsl:param name="lang.innovation-potential.applicability-efficiency" select="'Effizienz'"/>
<xsl:param name="lang.innovation-potential.applicability-practicability" select="'Praktikabilität und Anschlussfähigkeit'"/>
<xsl:param name="lang.innovation-potential.applicability-requirements" select="'Vorraussetzungen'"/>
<xsl:param name="lang.innovation-potential.applicability-prospects" select="'Aussichten für eine Etablierung / Verbreitung / Übertragung'"/>
<xsl:param name="lang.innovation-potential.regionalscope-primaryfield" select="'Primärer Einsatzbereich'"/>
<xsl:param name="lang.innovation-potential.regionalscope-laterdissimination" select="'spätere Verbreitung'"/>
<xsl:param name="lang.innovation-potential.regionalscope-description" select="'Erläuterung'"/>
<xsl:param name="lang.innovation-potential.srltrl-development-project-start" select="'Entwicklungsstand bei Projektbeginn'"/>
<xsl:param name="lang.innovation-potential.srltrl-development-project-end" select="'Entwicklungsstand bei Projektende'"/>
<xsl:param name="lang.innovation-potential.srltrl-selection" select="'TRL/SRL?'"/>
<xsl:param name="lang.innovation-potential.srltrl-readiness-project-start" select="'Reifegrad bei Projektbeginn'"/>
<xsl:param name="lang.innovation-potential.srltrl-readiness-project-end" select="'Reifegrad bei Projektende'"/>

<xsl:param name="lang.innovationidea.description" select="'Bezeichnung'"/>

<xsl:param name="lang.application.related-innovation" select="'Verlinkung zur dokumentierten Lösung / Veränderung / Innovation oder einem Patent'"/>
<xsl:param name="lang.application.free-unit" select="'Einheit (eigene Angabe)'"/>
<xsl:param name="lang.application.reference-year" select="'Referenzjahr'"/>
<xsl:param name="lang.application.quantity" select="'Menge / Anzahl (Steigerung)'"/>
<xsl:param name="lang.application.regionaloutreach" select="'Region der Anwendung'"/>
<xsl:param name="lang.application.success-description" select="'Anmerkungen / Weblink'"/>

<xsl:param name="lang.unexpected-result.result-description" select="'Beschreibung Ergebniss / Problem'"/>
<xsl:param name="lang.unexpected-result.status-publication" select="'Veröffentlichung vorgesehen'"/>
<xsl:param name="lang.unexpected-result.information-publication" select="'Information zur Veröffentlichung'"/>
<xsl:param name="lang.unexpected-result.link-publication" select="'Titel der Veröffentlichung'"/>

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

<xsl:param name="lang.spinoff.result-registercourt" select="'Ort des zuständigen Gerichts'"/>
<xsl:param name="lang.spinoff.result-registertype" select="'Registerart'"/>
<xsl:param name="lang.spinoff.result-registernumber" select="'Registernummer'"/>
<xsl:param name="lang.spinoff.result-foundingdate" select="'Datum der Eintragung'"/>
<xsl:param name="lang.spinoff.result-type" select="'Art der Ausgründung'"/>
<xsl:param name="lang.spinoff.result-nace" select="'Wirtschaftszweig'"/>
<xsl:param name="lang.spinoff.result-webaddress" select="'Homepage der Ausgründung'"/>
<xsl:param name="lang.spinoff.result-relatedorganisation" select="'Ausgründende Organisation(en)'"/>
<xsl:param name="lang.spinoff.funding-sectiontitle" select="'Unterstützungsleistung'"/>
<xsl:param name="lang.spinoff.funding-title" select="'Förderprogramm'"/>
<xsl:param name="lang.spinoff.funding-amount" select="'Fördersumme'"/>
<xsl:param name="lang.spinoff.funding-description" select="'Nicht finanzielle Unterstützung'"/>
<xsl:param name="lang.spinoff.performance-title" select="'Wirtschaftliche Entwicklung'"/>
<xsl:param name="lang.spinoff.performance-amount" select="'Jahresumsatz'"/>

<xsl:param name="lang.step.partner" select="'Beteiligte Projektpartner/Unterauftragsnehmer/Kooperationspartner'"/>
<xsl:param name="lang.step.target-group" select="'Beteiligte Akteursgruppen'"/>
<xsl:param name="lang.step.type" select="'Art der Arbeit'"/>
<xsl:param name="lang.step.date" select="'Zeithorizont für die Realisierung'"/>

<xsl:param name="lang.award.date" select="'Datum der Verleihung'"/>
<xsl:param name="lang.award.pricemoney" select="'Preisgeld'"/>
<xsl:param name="lang.award.winner" select= "'Ausgezeichnete Person(en)'"/>
<xsl:param name="lang.award.organisation" select= "'Ausgezeichnete Organisation(en)'"/>
<xsl:param name="lang.award.donor" select= "'Initiator(en)'"/>
<xsl:param name="lang.award.target-group" select= "'Akteursgruppe des Initiators'"/>

<xsl:param name="lang.condition.conditiontype" select="'Art der Rahmenbedingung'"/>
<xsl:param name="lang.condition.influence" select="'Einfluss auf die Wirkung des Projektes'"/>
<xsl:param name="lang.condition.startframework" select="'Startzeitpunkt der Wirksamkeit'"/>

<xsl:param name="lang.expected-societal-impact.transitionarea" select="'Transformationsbereich'"/>
<xsl:param name="lang.expected-societal-impact.evidence" select="'Einfluss auf die Wirkung des Projektes'"/>
<xsl:param name="lang.expected-societal-impact.type" select="'Art des Einflusses'"/>
<xsl:param name="lang.expected-societal-impact.type-coreelement" select="'Kernelement'"/>
<xsl:param name="lang.expected-societal-impact.type-intensity" select="'Wirkungsintensität'"/>
<xsl:param name="lang.expected-societal-impact.type-description" select="'Umfang der erwarteten Wirkung'"/>

<xsl:param name="lang.negative-side-effect.transitionArea" select="'Adressierter Transformationsbereich'"/>
<xsl:param name="lang.negative-side-effect.evidence" select="'Fachliche Grundlage'"/>
<xsl:param name="lang.negative-side-effect.type" select="'Art des Einflusses'"/>
<xsl:param name="lang.negative-side-effect.type-coreelement" select="'Kernelement der Nachhaltigkeit'"/>
<xsl:param name="lang.negative-side-effect.type-intensity" select="'Intensität der negativen Nebenwirkung'"/>
<xsl:param name="lang.negative-side-effect.type-description" select="'Umfang der negativen Wirkung'"/>

<xsl:param name="lang.ethic.key-message" select="'Ethische Reflexion'"/>
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

<xsl:param name="lang.overview.task" select="'Aufgaben'"/>
<xsl:param name="lang.overview.event" select="'Events'"/>
<xsl:param name="lang.overview.process-event" select="'Kooperations-prozesse'"/>
<xsl:param name="lang.overview.physical-object" select="'Objekte/Materialien'"/>
<xsl:param name="lang.overview.planned-publication" select="'Geplante Veröffentlichungen'"/>
<xsl:param name="lang.overview.publication" select="'Veröffentlichungen'"/>
<xsl:param name="lang.overview.product" select="'Forschungsdaten'"/>
<xsl:param name="lang.overview.innovation-potential" select="'Lösung/Veränderung/Innovation'"/>
<xsl:param name="lang.overview.innovation-idea" select="'Ideen für eine Lösung/Veränderung/Innovation'"/>
<xsl:param name="lang.overview.application" select="'Anwendung'"/>
<xsl:param name="lang.overview.unexpected-result" select="'Unerwartete Erkentnisse und Probleme'"/>
<xsl:param name="lang.overview.patent" select="'Patente'"/>
<xsl:param name="lang.overview.spinoff" select="'Aus-gründungen'"/>
<xsl:param name="lang.overview.further-steps" select="'Arbeiten und Kooperationen nach Projektende'"/>
<xsl:param name="lang.overview.award" select="'Aus-zeichnungen und Preise'"/>
<xsl:param name="lang.overview.open-research-question" select="'Offene Forschungs-fragen'"/>
<xsl:param name="lang.overview.project-partner" select="'Projektpartner'"/>
<xsl:param name="lang.overview.cooperation-partner" select="'Unterauftragnehmer und Kooperationspartner'"/>
<xsl:param name="lang.overview.target-group" select="'Akteursgruppen'"/>

<xsl:param name="lang.cooperation" select="'Partner'"/>
<xsl:param name="lang.cooperation.sector" select="'Sektor'"/>
<xsl:param name="lang.cooperation.nace-destatis" select="'Wirtschaftszweig / Disziplin'"/>
<xsl:param name="lang.cooperation.political-level" select="'Politische Ebene'"/>

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
<xsl:param name="font.size.key-value" select="'11pt'"/>
<xsl:param name="font.size.label-below" select="'8pt'"/> 

<!-- font weigths -->
<xsl:param name="font.weight.title" select="'bold'"/>
<xsl:param name="font.weight.key-value" select="'bold'"/>
<xsl:param name="font.weight.label" select="'bold'"/>
<xsl:param name="font.weight.value" select="'normal'"/> 
 
<!-- alignments -->
<xsl:param name="text.alignment.title" select="'left'"/>
<xsl:param name="text.alignment.label" select="'left'"/>
<xsl:param name="text.alignment.value" select="'left'"/>

<!-- margins -->	
<xsl:param name="margin.top.title" select="'5mm'"/>	
<xsl:param name="margin.top.main" select="'2mm'"/>
<xsl:param name="margin.top.section" select="'10mm'"/>
<xsl:param name="margin.top.gap" select="'5mm'"/>
<xsl:param name="margin.top.label-table" select="'-2mm'"/> <!-- general space above a label in a table context -->
<xsl:param name="margin.top.label-table-start" select="'1mm'"/> <!-- space above the first label in a table context -->
<xsl:param name="margin.top.label-below" select="'0mm'"/> <!-- space between label and value below in a box context-->
<xsl:param name="margin.top.label-below-table" select="'-3mm'"/> <!-- space between label and value below in a table context-->
<xsl:param name="margin.bottom.section" select="'-3mm'"/>
<xsl:param name="margin.left.table-cell" select="'0mm'"/> <!-- when tables without lines are used inside blocks then text is aligned quite right -->
                                                          <!-- not in line with the block text, see publication template -->
<xsl:param name="margin.left.border" select="'3mm'"/>
<xsl:param name="margin.right.border" select="'2mm'"/>

<!-- paddings -->	
<xsl:param name="padding.before.border" select="'1mm'"/>
<xsl:param name="padding.after.border" select="'2mm'"/>
<xsl:param name="padding.start.border" select="'2mm'"/>
<xsl:param name="padding.end.border" select="'2mm'"/>

<!-- lengths -->
<xsl:param name="length.ruler" select="'100%'"/>

<!-- widths -->
<xsl:param name="width.border" select="'0.3mm'"/>
<xsl:param name="width.table" select="'97%'"/>
<xsl:param name="width.ruler" select="'100%'"/>
<xsl:param name="width.icon.overview" select="'12mm'"/>

<!-- colours -->
<xsl:param name="colour.border" select="'#808080'"/>
<xsl:param name="colour.label" select="'#808080'"/>

<!-- styles -->
<xsl:param name="style.border" select="'solid'"/>
<xsl:param name="style.ruler" select="'solid'"/>

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
					  <xsl:value-of select="$lang.page"/>
       			<fo:page-number/>
     			</fo:block>
   			</fo:static-content>

				<!-- start page flow -->
				<fo:flow flow-name="xsl-region-body">

          <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
					<!-- SECTION #-1 - TESTING ON THE FIRST PAGE --> 
          <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->

					<xsl:call-template name="research-work"/>

          <!-- INFO: there is so far no application for the graphs, tests are successful --> 
					<!-- <xsl:call-template name="graphs">
						<xsl:with-param name="imageDir" select="$imageDir" />                      
					</xsl:call-template> -->

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
						<xsl:with-param name="title" select="$lang.title.section2"/>
						<xsl:with-param name="fontSize" select="$font.size.section-title"/>
					</xsl:call-template>

				  <!-- sub section title - state of the art in science and technology / previous works -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section2.1"/>
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
						<xsl:with-param name="title" select="$lang.title.section3"/>
						<xsl:with-param name="fontSize" select="$font.size.section-title"/>
					</xsl:call-template>

				  <!-- sub section title - resource planning of the project -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section3.1"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-section-title"/>
					</xsl:call-template>

				  <!-- sub sub section title - overview working plan -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section3.1.1"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>
					
          <xsl:call-template name="working-plan"/>

				  <!-- sub sub section title - work packages and tasks -->					
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section3.1.2"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

					<xsl:call-template name="work-package"/>

				  <!-- sub sub section title - research work -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section3.1.3"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

          <!-- TODO: revise template -->
          <!-- approach: one box per workpackage -->
					<!-- approach: two column table -->
					<!-- approach: left: title -->
					<!-- approach: right: all metadata -->
					<!-- entities to consider: TASK, EVENT, PROCESSEVENT, PLANNEDPUBLICATION, OBJECT/MATERIALS -->

					<xsl:call-template name="research-work"/>

				  <!-- sub sub section title - publications -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section3.1.4"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

          <xsl:call-template name="publication"/>

				  <!-- sub sub section title - publications -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section3.1.5"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

          <xsl:call-template name="product"/>

				  <!-- sub sub section title - interaction and transfer -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section3.1.6"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

          <xsl:call-template name="interaction-transfer-overview"/>

				  <!-- sub section title - milestone planning -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section3.2"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-section-title"/>
					</xsl:call-template>

					<xsl:call-template name="milestone"/>

				  <!-- sub section title - in-depth material and methods -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section3.3"/>
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

 					<xsl:call-template name="expected-societal-impact"/>

				  <!-- sub sub section title - description of potential negative side effects -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section4.3.3"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

				 <xsl:call-template name="negative-side-effect"/> 

				  <!-- sub sub section title - description of ethical aspects -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section4.3.4"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
					</xsl:call-template>

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
						<xsl:with-param name="title" select="$lang.title.section5"/>
						<xsl:with-param name="fontSize" select="$font.size.section-title"/>
					</xsl:call-template>

				  <!-- sub section title - cooperation overview -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="$lang.title.section5.1"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-section-title"/>
					</xsl:call-template>		

          <xsl:call-template name="cooperation-overview"/>

				  <!-- sub section title - cooperation partners -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="lang.title.section5.2"/>
						<xsl:with-param name="fontSize" select="$font.size.sub-section-title"/>
					</xsl:call-template>		

					<xsl:call-template name="cooperation-partner"/>

				  <!-- sub section title - relevant stakeholder groups of the project -->
					<xsl:call-template name="title">
						<xsl:with-param name="title" select="lang.title.section5.3"/>
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
						<xsl:with-param name="title" select="$lang.title.section6"/>
						<xsl:with-param name="fontSize" select="$font.size.section-title"/>
					</xsl:call-template>


				</fo:flow>

			</fo:page-sequence>

	  <!--==========================================================--> 
		<!-- PDF EMBEDDING-->
		<!--==========================================================--> 
            
			<xsl:call-template name="embedded-pdf">
				<xsl:with-param name="imageDir" select="$imageDir" />
			</xsl:call-template>

		</fo:root>
	</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE PROJECT ENTITY -->	
<!--########################################################################-->

 <!-- TODO: adjust indentation of all code -->
<!-- TODO: bring the templates in the order as they occur, only exception should be the parts for project-partner -->

	<xsl:template name="project">

		<xsl:call-template name="label-value-single">
			<xsl:with-param name="label" select="'Akronym'"/>
			<xsl:with-param name="value" select="cerif:Acronym"/>
		</xsl:call-template>

		<xsl:call-template name="label-period">
			<xsl:with-param name="label" select="$lang.term"/>
			<xsl:with-param name="startDate" select="cerif:StartDate"/>
			<xsl:with-param name="endDate" select="cerif:EndDate"/>
			<xsl:with-param name="duration" select="cerif:Duration"/>
			<xsl:with-param name="addRuler" select="'false'"/>
		</xsl:call-template>

		<xsl:call-template name="label-value-single">
			<xsl:with-param name="label" select="$lang.funding.call"/>
			<xsl:with-param name="value" select="cerif:FundingCall"/>
			<xsl:with-param name="addRuler" select="'false'"/>
		</xsl:call-template>

		<xsl:call-template name="label-value-single">
			<xsl:with-param name="label" select="$lang.funding.programme"/>
			<xsl:with-param name="value" select="cerif:FundingProgramme"/>
			<xsl:with-param name="addRuler" select="'false'"/>
		</xsl:call-template>			
	
	</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE PROJECT PARTNER ENTITY -->	
<!--########################################################################-->

	<xsl:template name="project-partner">

		<xsl:if test="cerif:ProjectPartner">

			<xsl:call-template name="vertical-gap"/>  <!-- make a small gap first to indicate a separation from the items above-->

			<xsl:call-template name="label-block">  <!-- partner label -->
				<xsl:with-param name="label" select="'Partner'"/>
				<xsl:with-param name="separator" select="':'"/>
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
						<xsl:with-param name="addRuler" select="'false'"/>
					</xsl:call-template>									

					<xsl:call-template name="ruler"/>

					<xsl:call-template name="value-single">
						<xsl:with-param name="value" select="cerif:Import/cerif:ParentOrganisation"/>
						<xsl:with-param name="addRuler" select="'false'"/>
				  </xsl:call-template>	

					<xsl:call-template name="value-single"> 
						<xsl:with-param name="value" select="cerif:Import/cerif:OrganisationName"/>
						<xsl:with-param name="addRuler" select="'false'"/>
				  </xsl:call-template>					

					<xsl:call-template name="value-postcode-city-country">
						<xsl:with-param name="postCode" select="cerif:Import/cerif:PostCode"/>
						<xsl:with-param name="city" select="cerif:Import/cerif:City"/>
						<xsl:with-param name="country" select="cerif:Import/cerif:Country"/>
						<xsl:with-param name="addRuler" select="'false'"/>
				  </xsl:call-template>	

					<xsl:call-template name="value-comma-list"> 
						<xsl:with-param name="value" select="cerif:Import/cerif:WebAddress"/>
						<xsl:with-param name="addRuler" select="'false'"/>
				  </xsl:call-template>	

	        <xsl:if test="cerif:ImportLead/cerif:Name">
						<xsl:call-template name="vertical-gap"/>
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

          <xsl:if test="cerif:ImportLead/cerif:Name">
						<xsl:call-template name="vertical-gap"/>

						<xsl:call-template name="label-value-single">
							<xsl:with-param name="label" select="$lang.project-partner.easy-online"/>
							<xsl:with-param name="value" select="cerif:EasyOnlineImport"/>
							<xsl:with-param name="addRuler" select="'false'"/>
						</xsl:call-template>						
					</xsl:if>

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

			<xsl:call-template name="label-value-single">
				<xsl:with-param name="label" select="$lang.description"/>
				<xsl:with-param name="value" select="cerif:ImpactPathway/cerif:Index/cerif:Description"/>
			</xsl:call-template>

			</xsl:if> 

		<!-- handle case if more than one impact pathway exists -->
			<xsl:if test="count(cerif:ImpactPathway/cerif:Index) > 1"> 
				<xsl:for-each select="cerif:ImpactPathway/cerif:Index">

					<xsl:call-template name="label-block">  
						<xsl:with-param name="label" select="cerif:Title"/>
					</xsl:call-template>	

					<xsl:call-template name="label-value-single">
						<xsl:with-param name="label" select="$lang.description"/>
						<xsl:with-param name="value" select="cerif:Description"/>
					</xsl:call-template>

				</xsl:for-each>

			</xsl:if> 

	  </xsl:if>
		
		<!-- TODO: correctly implement figures for IPW-->
		<!-- <xsl:call-template name="impactpathway-screenshots">
			<xsl:with-param name="imageDir" select="$imageDir"/>
		</xsl:call-template> -->

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

						<xsl:call-template name="value-icon-count-table"> 
							<xsl:with-param name="iconFileName" select="'event.png'"/>
							<xsl:with-param name="count" select="count(cerif:Event/cerif:Index)"/>
						</xsl:call-template>	

					</fo:table-cell>

          <!-- process event -->
					<fo:table-cell column-number="3" 
												 border-width="{$width.border}"
												 border-color="{$colour.border}"
												 border-style="{$style.border}">

						<xsl:call-template name="value-icon-count-table"> 
							<xsl:with-param name="iconFileName" select="'process.png'"/>
							<xsl:with-param name="count" select="count(cerif:ProcessEvent/cerif:Index)"/>
						</xsl:call-template>	

					</fo:table-cell>

          <!-- physical object -->
					<fo:table-cell column-number="4" 
												 border-width="{$width.border}"
												 border-color="{$colour.border}"
												 border-style="{$style.border}">

						<xsl:call-template name="value-icon-count-table"> 
							<xsl:with-param name="iconFileName" select="'object-material.png'"/>
							<xsl:with-param name="count" select="count(cerif:PhysicalObject/cerif:Index)"/>
						</xsl:call-template>	

					</fo:table-cell>

          <!-- planned publication -->
					<fo:table-cell column-number="5" 
												 border-width="{$width.border}"
												 border-color="{$colour.border}"
												 border-style="{$style.border}">

						<xsl:call-template name="value-icon-count-table"> 
							<xsl:with-param name="iconFileName" select="'publication.png'"/>
							<xsl:with-param name="count" select="count(cerif:PlannedPublication/cerif:Index)"/>
						</xsl:call-template>	

					</fo:table-cell>

          <!-- publication -->
					<fo:table-cell column-number="6" 
												 border-width="{$width.border}"
												 border-color="{$colour.border}"
												 border-style="{$style.border}">

						<xsl:call-template name="value-icon-count-table"> 
							<xsl:with-param name="iconFileName" select="'publication.png'"/>
							<xsl:with-param name="count" select="count(cerif:Publication/cerif:Index)"/>
						</xsl:call-template>	

					</fo:table-cell>

          <!-- product -->
					<fo:table-cell column-number="7" 
												 border-width="{$width.border}"
												 border-color="{$colour.border}"
												 border-style="{$style.border}">

						<xsl:call-template name="value-icon-count-table"> 
							<xsl:with-param name="iconFileName" select="'object-material.png'"/>
							<xsl:with-param name="count" select="count(cerif:Product/cerif:Index)"/>
						</xsl:call-template>	

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

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.overview.event"/>
								<xsl:with-param name="alignment" select="'center'"/>
							</xsl:call-template>	

						</fo:table-cell>

          	<!-- process event -->
						<fo:table-cell column-number="3"
													 border-width="{$width.border}"
													 border-color="{$colour.border}"
													 border-style="{$style.border}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.overview.process-event"/>
								<xsl:with-param name="alignment" select="'center'"/>
							</xsl:call-template>	

						</fo:table-cell>

      			<!-- physical object -->
						<fo:table-cell column-number="4"
													 border-width="{$width.border}"
													 border-color="{$colour.border}"
													 border-style="{$style.border}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.overview.physical-object"/>
								<xsl:with-param name="alignment" select="'center'"/>
							</xsl:call-template>	

						</fo:table-cell>

      			<!-- planned publication -->
						<fo:table-cell column-number="5"
													 border-width="{$width.border}"
													 border-color="{$colour.border}"
													 border-style="{$style.border}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.overview.planned-publication"/>
								<xsl:with-param name="alignment" select="'center'"/>
							</xsl:call-template>	

						</fo:table-cell>

          	<!-- publication -->
						<fo:table-cell column-number="6"
													 border-width="{$width.border}"
													 border-color="{$colour.border}"
													 border-style="{$style.border}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.overview.publication"/>
								<xsl:with-param name="alignment" select="'center'"/>
							</xsl:call-template>	

						</fo:table-cell>

          	<!-- product -->
						<fo:table-cell column-number="7"
													 border-width="{$width.border}"
													 border-color="{$colour.border}"
													 border-style="{$style.border}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.overview.product"/>
								<xsl:with-param name="alignment" select="'center'"/>
							</xsl:call-template>	

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

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.date"/>
								<xsl:with-param name="fontSize" select="$font.size.key-value"/>
								<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
								<xsl:with-param name="alignment" select="'center'"/>
							</xsl:call-template>	

						</fo:table-cell>

            <!-- title -->
						<fo:table-cell column-number="3" 
													 border-width="{$width.border}"
													 border-color="{$colour.border}"
													 border-style="{$style.border}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.title"/>
								<xsl:with-param name="fontSize" select="$font.size.key-value"/>
								<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
							</xsl:call-template>	

						</fo:table-cell>

            <!-- description -->
						<fo:table-cell column-number="4" 
													 border-width="{$width.border}"
													 border-color="{$colour.border}"
													 border-style="{$style.border}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.description"/>
								<xsl:with-param name="fontSize" select="$font.size.key-value"/>
								<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
							</xsl:call-template>	

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

									<xsl:call-template name="value-single-table"> 
										<xsl:with-param name="value" select="cerif:EndDate"/>
										<xsl:with-param name="alignment" select="'center'"/>
									</xsl:call-template>	

								</fo:table-cell>

								<!-- title -->
								<fo:table-cell column-number="3"
												 			 border-width="{$width.border}"
												 			 border-color="{$colour.border}"
															 border-style="{$style.border}">

									<xsl:call-template name="value-single-table"> 
										<xsl:with-param name="value" select="cerif:Title"/>
									</xsl:call-template>	

								</fo:table-cell>

								<!-- description -->
								<fo:table-cell column-number="4"
												 			 border-width="{$width.border}"
												 			 border-color="{$colour.border}"
															 border-style="{$style.border}">

									<xsl:call-template name="value-single-table"> 
										<xsl:with-param name="value" select="cerif:Description"/>
									</xsl:call-template>	

								</fo:table-cell>

							</fo:table-row>

						</xsl:for-each>

					</fo:table-body>					

        </fo:table>

			</fo:block>

		</xsl:if>

	</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE WORKING PLAN PACKAGES -->	
<!--########################################################################-->

  <xsl:template name="working-plan">

	  <!-- TODO: correctly implement pictures for working plan -->
		<!-- <xsl:call-template name="workingplan-screenshots">
			<xsl:with-param name="imageDir" select="$imageDir"/>
		</xsl:call-template> -->

  </xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE WORK PACKAGES -->	
<!--########################################################################-->

  <xsl:template name="work-package">

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

					<xsl:call-template name="value-single">
						<xsl:with-param name="value" select="cerif:Title"/>
						<xsl:with-param name="fontSize" select="$font.size.key-value"/>
						<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
						<xsl:with-param name="addRuler" select="'false'"/>
					</xsl:call-template>									

					<xsl:call-template name="label-value-single-below">	
						<xsl:with-param name="label" select="$lang.description"/>
						<xsl:with-param name="value" select="cerif:Description"/>
						<xsl:with-param name="checkValue" select="'false'"/>
					</xsl:call-template>

					<xsl:call-template name="label-value-single-below">	
						<xsl:with-param name="label" select="$lang.work-package.requirement"/>
						<xsl:with-param name="value" select="cerif:Requirement"/>
						<xsl:with-param name="checkValue" select="'false'"/>											
					</xsl:call-template>

					<xsl:call-template name="label-value-single-below">	
						<xsl:with-param name="label" select="$lang.work-package.responsible-organisation"/>
						<xsl:with-param name="value" select="cerif:ResponsibleOrganisation"/>
						<xsl:with-param name="checkValue" select="'false'"/>										
					</xsl:call-template>
		
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
	
	      <!-- check if the work package as any associated entities -->
	      <xsl:if test="count(cerif:Task/cerif:Index) > 0 or count(cerif:Event/cerif:Index) > 0 or count(cerif:ProcessEvent/cerif:Index) > 0 or count(cerif:PlannedPublication/cerif:Index) > 0 or count(cerif:PhysicalObject/cerif:Index) > 0">
					
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
							<xsl:with-param name="addRuler" select="'false'"/>
						</xsl:call-template>

						<xsl:call-template name="ruler"/>

						<!-- consider tasks -->
						<xsl:if test="count(cerif:Task/cerif:Index) > 0">
              <xsl:call-template name="research-work-task">
                <xsl:with-param name="entity" select="cerif:Task/cerif:Index"/>
							</xsl:call-template>	
						</xsl:if>

						<!-- consider events -->
						<xsl:if test="count(cerif:Event/cerif:Index) > 0">
              <xsl:call-template name="research-work-event">
                <xsl:with-param name="entity" select="cerif:Event/cerif:Index"/>
							</xsl:call-template>							
						</xsl:if>

						<!-- consider process events -->
						<xsl:if test="count(cerif:ProcessEvent/cerif:Index) > 0">
              <xsl:call-template name="research-work-process-event">
                <xsl:with-param name="entity" select="cerif:ProcessEvent/cerif:Index"/>
							</xsl:call-template>	
						</xsl:if>

						<!-- consider planned publications -->
						<xsl:if test="count(cerif:PlannedPublication/cerif:Index) > 0">
              <xsl:call-template name="research-work-planned-publication">
                <xsl:with-param name="entity" select="cerif:PlannedPublication/cerif:Index"/>
							</xsl:call-template>	
						</xsl:if>

						<!-- consider physical objects -->
						<xsl:if test="count(cerif:PhysicalObject/cerif:Index) > 0">
              <xsl:call-template name="research-work-physical-object">
                <xsl:with-param name="entity" select="cerif:PhysicalObject/cerif:Index"/>
							</xsl:call-template>	
						</xsl:if>

					</fo:block> 		

        </xsl:if> 

		  </xsl:for-each> 
	
		</xsl:if>
	
	</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE TASK ENTITY (FOR THE RESEARCH WORK) -->	
<!--########################################################################-->

  <xsl:template name="research-work-task">
    <xsl:param name="entity"/>

    <!-- entity name -->
		<xsl:call-template name="value-single">
			<xsl:with-param name="value" select="$lang.research-work.task"/>
			<xsl:with-param name="fontSize" select="$font.size.standard"/>
			<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
			<xsl:with-param name="addRuler" select="'false'"/>
		</xsl:call-template>

		<!-- table-->
		<fo:table table-layout="fixed" 
							vertical-align="middle"
							border-before-style="hidden" 
							border-after-style="hidden"
							border-start-style="hidden"
							border-end-style="hidden">

			<!-- define the table columns -->
			<fo:table-column column-width="proportional-column-width(1)"/>	<!-- this construct is used to align the table in a centred way	-->						
			<fo:table-column column-width="40.0%"/>
			<fo:table-column column-width="60.0%"/>
			<fo:table-column column-width="proportional-column-width(1)"/>  <!-- this is also part of central alignment approach --> 

			<!-- table body (table header is omitted here) -->
 		  <fo:table-body>	

				<xsl:for-each select="$entity">	

					<fo:table-row>

						<!-- left column -->
						<fo:table-cell column-number="2"
													 border-width="{$width.border}"
													 border-color="{$colour.border}"
													 border-style="{$style.border}"
													 border-start-style="hidden"
													 margin-left="{$margin.left.table-cell}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="cerif:Title"/>
							</xsl:call-template>	

						</fo:table-cell>

						<!-- right column -->
						<fo:table-cell column-number="3"
													 border-width="{$width.border}"
													 border-color="{$colour.border}"
													 border-style="{$style.border}"
													 border-end-style="hidden"
													 margin-left="{$margin.left.table-cell}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="cerif:Description"/>
							</xsl:call-template>	

						</fo:table-cell>

					</fo:table-row>

				</xsl:for-each> 

			</fo:table-body>

		</fo:table>

  </xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE EVENT ENTITY (FOR THE RESEARCH WORK) -->	
<!--########################################################################-->

  <xsl:template name="research-work-event">
    <xsl:param name="entity"/>

    <!-- entity name -->
		<xsl:call-template name="value-single">
			<xsl:with-param name="value" select="$lang.research-work.event"/>
			<xsl:with-param name="fontSize" select="$font.size.standard"/>
			<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
			<xsl:with-param name="addRuler" select="'false'"/>
		</xsl:call-template>

		<!-- table-->
		<fo:table table-layout="fixed" 
							vertical-align="middle"
							border-before-style="hidden" 
							border-after-style="hidden"
							border-start-style="hidden"
							border-end-style="hidden">

			<!-- define the table columns -->
			<fo:table-column column-width="proportional-column-width(1)"/>	<!-- this construct is used to align the table in a centred way	-->						
			<fo:table-column column-width="40.0%"/>
			<fo:table-column column-width="60.0%"/>
			<fo:table-column column-width="proportional-column-width(1)"/>  <!-- this is also part of central alignment approach --> 

			<!-- table body (table header is omitted here) -->
 		  <fo:table-body>	

				<xsl:for-each select="$entity">	

					<fo:table-row>

						<!-- left column -->
						<fo:table-cell column-number="2"
													 border-width="{$width.border}"
													 border-color="{$colour.border}"
													 border-style="{$style.border}"
													 border-start-style="hidden"
													 margin-left="{$margin.left.table-cell}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="cerif:Title"/>
							</xsl:call-template>	

						</fo:table-cell>

						<!-- right column -->
						<fo:table-cell column-number="3"
													 border-width="{$width.border}"
													 border-color="{$colour.border}"
													 border-style="{$style.border}"
													 border-end-style="hidden"
													 margin-left="{$margin.left.table-cell}">

              <!-- this approach is necessary to avoid problems with empty metadata --> 
							<xsl:choose>
                <xsl:when test="cerif:Description or cerif:Type or cerif:ProjectContribution or cerif:Purpose or cerif:InvolvedOrganisation or cerif:TargetGroup or cerif:Result/cerif:StartDate or cerif:Result/cerif:EndDate or cerif:Result/cerif:Duration or cerif:Result/cerif:ParticipantNumber or cerif:Result/cerif:City or cerif:Result/cerif:Country">

                  <!-- if the space above a label-value pair is reduced generally, there are problems for the first entry --> 
									<xsl:call-template name="vertical-gap">
									  <xsl:with-param name="label" select="$margin.top.label-table-start"/>
									</xsl:call-template>

									<xsl:call-template name="label-value-single-below-table"> 
										<xsl:with-param name="label" select="$lang.description"/>
										<xsl:with-param name="value" select="cerif:Description"/>
									</xsl:call-template>

									<xsl:call-template name="label-value-single-below-table"> 
										<xsl:with-param name="label" select="$lang.type"/>
										<xsl:with-param name="value" select="cerif:Type"/>
									</xsl:call-template>

									<xsl:call-template name="label-value-single-below-table"> 
										<xsl:with-param name="label" select="$lang.event.project-contribution"/>
										<xsl:with-param name="value" select="cerif:ProjectContribution"/>
									</xsl:call-template>

									<xsl:call-template name="label-value-comma-list-below-table"> 
										<xsl:with-param name="label" select="$lang.event.purpose"/>
										<xsl:with-param name="value" select="cerif:Purpose"/>
									</xsl:call-template>

									<xsl:call-template name="label-value-comma-list-below-table"> 
										<xsl:with-param name="label" select="$lang.event.involved-organisation"/>
										<xsl:with-param name="value" select="cerif:InvolvedOrganisation"/>
									</xsl:call-template>

									<xsl:call-template name="label-value-comma-list-below-table"> 
										<xsl:with-param name="label" select="$lang.event.target-group"/>
										<xsl:with-param name="value" select="cerif:TargetGroup"/>
									</xsl:call-template>

									<xsl:call-template name="label-period-below-table"> 
										<xsl:with-param name="label" select="$lang.period"/>
										<xsl:with-param name="startDate" select="cerif:Result/cerif:StartDate"/>
										<xsl:with-param name="endDate" select="cerif:Result/cerif:EndDate"/>
										<xsl:with-param name="duration" select="cerif:Result/cerif:Duration"/>
									</xsl:call-template>

									<xsl:call-template name="label-value-single-below-table"> 
										<xsl:with-param name="label" select="$lang.event.participant-number"/>
										<xsl:with-param name="value" select="cerif:Result/cerif:ParticipantNumber"/>
									</xsl:call-template>

									<xsl:call-template name="label-postcode-city-country-below-table"> 
										<xsl:with-param name="label" select="$lang.postcode-city-country"/>
										<xsl:with-param name="city" select="cerif:Result/cerif:City"/>
										<xsl:with-param name="country" select="cerif:Result/cerif:Country"/>
									</xsl:call-template>									

	 							</xsl:when>
  						  <xsl:otherwise>
									<xsl:call-template name="empty-table-cell-catcher"/>
						    </xsl:otherwise>
							</xsl:choose>

						</fo:table-cell>

					</fo:table-row>

				</xsl:for-each> 

			</fo:table-body>

		</fo:table>

  </xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE PROCESS EVENT ENTITY (FOR THE RESEARCH WORK) -->	
<!--########################################################################-->

  <xsl:template name="research-work-process-event">
    <xsl:param name="entity"/>

    <!-- entity name -->
		<xsl:call-template name="value-single">
			<xsl:with-param name="value" select="$lang.research-work.process-event"/>
			<xsl:with-param name="fontSize" select="$font.size.standard"/>
			<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
			<xsl:with-param name="addRuler" select="'false'"/>
		</xsl:call-template>

		<!-- table-->
		<fo:table table-layout="fixed" 
							vertical-align="middle"
							border-before-style="hidden" 
							border-after-style="hidden"
							border-start-style="hidden"
							border-end-style="hidden">

			<!-- define the table columns -->
			<fo:table-column column-width="proportional-column-width(1)"/>	<!-- this construct is used to align the table in a centred way	-->						
			<fo:table-column column-width="40.0%"/>
			<fo:table-column column-width="60.0%"/>
			<fo:table-column column-width="proportional-column-width(1)"/>  <!-- this is also part of central alignment approach --> 

			<!-- table body (table header is omitted here) -->
 		  <fo:table-body>	

				<xsl:for-each select="$entity">	

					<fo:table-row>

						<!-- left column -->
						<fo:table-cell column-number="2"
													 border-width="{$width.border}"
													 border-color="{$colour.border}"
													 border-style="{$style.border}"
													 border-start-style="hidden"
													 margin-left="{$margin.left.table-cell}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="cerif:Title"/>
							</xsl:call-template>	

						</fo:table-cell>

						<!-- right column -->
						<fo:table-cell column-number="3"
													 border-width="{$width.border}"
													 border-color="{$colour.border}"
													 border-style="{$style.border}"
													 border-end-style="hidden"
													 margin-left="{$margin.left.table-cell}">

              <!-- this approach is necessary to avoid problems with empty metadata --> 
							<xsl:choose>
                <xsl:when test="cerif:Description or cerif:Purpose or cerif:InvolvedOrganisation or cerif:TargetGroup">  
								
								<!-- or cerif:Result/cerif:StartDate or cerif:Result/cerif:EndDate or cerif:Result/cerif:Duration or cerif:Result/cerif:ParticipantNumber or cerif:Result/cerif:City or cerif:Result/cerif:Country"-->

                  <!-- if the space above a label-value pair is reduced generally, there are problems for the first entry --> 
									<xsl:call-template name="vertical-gap">
									  <xsl:with-param name="label" select="$margin.top.label-table-start"/>
									</xsl:call-template>

									<xsl:call-template name="label-value-single-below-table"> 
										<xsl:with-param name="label" select="$lang.description"/>
										<xsl:with-param name="value" select="cerif:Description"/>
									</xsl:call-template>

									<xsl:call-template name="label-value-comma-list-below-table"> 
										<xsl:with-param name="label" select="$lang.process-event.purpose"/>
										<xsl:with-param name="value" select="cerif:Purpose"/>
									</xsl:call-template>

									<xsl:call-template name="label-value-comma-list-below-table"> 
										<xsl:with-param name="label" select="$lang.process-event.involved-organisation"/>
										<xsl:with-param name="value" select="cerif:InvolvedOrganisation"/>
									</xsl:call-template>

									<xsl:call-template name="label-value-comma-list-below-table"> 
										<xsl:with-param name="label" select="$lang.process-event.target-group"/>
										<xsl:with-param name="value" select="cerif:TargetGroup"/>
									</xsl:call-template>


<!--

									<xsl:call-template name="label-period-below-table"> 
										<xsl:with-param name="label" select="$lang.period"/>
										<xsl:with-param name="startDate" select="cerif:Result/cerif:StartDate"/>
										<xsl:with-param name="endDate" select="cerif:Result/cerif:EndDate"/>
										<xsl:with-param name="duration" select="cerif:Result/cerif:Duration"/>
									</xsl:call-template>

									<xsl:call-template name="label-value-single-below-table"> 
										<xsl:with-param name="label" select="$lang.event.participant-number"/>
										<xsl:with-param name="value" select="cerif:Result/cerif:ParticipantNumber"/>
									</xsl:call-template>

									<xsl:call-template name="label-postcode-city-country-below-table"> 
										<xsl:with-param name="label" select="$lang.postcode-city-country"/>
										<xsl:with-param name="city" select="cerif:Result/cerif:City"/>
										<xsl:with-param name="country" select="cerif:Result/cerif:Country"/>
									</xsl:call-template>									
-->

	 							</xsl:when>
  						  <xsl:otherwise>
									<xsl:call-template name="empty-table-cell-catcher"/>
						    </xsl:otherwise>
							</xsl:choose>

						</fo:table-cell>

					</fo:table-row>

				</xsl:for-each> 

			</fo:table-body>

		</fo:table>

  </xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE PLANNED PUBLICATION ENTITY (FOR THE RESEARCH WORK) -->	
<!--########################################################################-->

  <xsl:template name="research-work-planned-publication">
    <xsl:param name="entity"/>

    <!-- entity name -->
		<xsl:call-template name="value-single">
			<xsl:with-param name="value" select="$lang.research-work.planned-publication"/>
			<xsl:with-param name="fontSize" select="$font.size.standard"/>
			<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
			<xsl:with-param name="addRuler" select="'false'"/>
		</xsl:call-template>

		<!-- table-->
		<fo:table table-layout="fixed" 
							vertical-align="middle"
							border-before-style="hidden" 
							border-after-style="hidden"
							border-start-style="hidden"
							border-end-style="hidden">

			<!-- define the table columns -->
			<fo:table-column column-width="proportional-column-width(1)"/>	<!-- this construct is used to align the table in a centred way	-->						
			<fo:table-column column-width="40.0%"/>
			<fo:table-column column-width="60.0%"/>
			<fo:table-column column-width="proportional-column-width(1)"/>  <!-- this is also part of central alignment approach --> 

			<!-- table body (table header is omitted here) -->
 		  <fo:table-body>	

				<xsl:for-each select="$entity">	

					<fo:table-row>

						<!-- left column -->
						<fo:table-cell column-number="2"
													 border-width="{$width.border}"
													 border-color="{$colour.border}"
													 border-style="{$style.border}"
													 border-start-style="hidden"
													 margin-left="{$margin.left.table-cell}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="cerif:Title"/>
							</xsl:call-template>	

						</fo:table-cell>

						<!-- right column -->
						<fo:table-cell column-number="3"
													 border-width="{$width.border}"
													 border-color="{$colour.border}"
													 border-style="{$style.border}"
													 border-end-style="hidden"
													 margin-left="{$margin.left.table-cell}">

							<!-- this approach is necessary to avoid problems with empty metadata --> 
							<xsl:choose>
                <xsl:when test="cerif:Description or cerif:TargetAudience">

                  <!-- if the space above a label-value pair is reduced generally, there are problems for the first entry --> 
									<xsl:call-template name="vertical-gap">
									  <xsl:with-param name="label" select="$margin.top.label-table-start"/>
									</xsl:call-template>

									<xsl:call-template name="label-value-single-below-table"> 
										<xsl:with-param name="label" select="$lang.description"/>
										<xsl:with-param name="value" select="cerif:Description"/>
									</xsl:call-template>

									<xsl:call-template name="label-value-comma-list-below-table"> 
										<xsl:with-param name="label" select="$lang.planned-publication.target-audience"/>
										<xsl:with-param name="value" select="cerif:TargetAudience"/>
									</xsl:call-template>

	 							</xsl:when>
  						  <xsl:otherwise>
									<xsl:call-template name="empty-table-cell-catcher"/>
						    </xsl:otherwise>
							</xsl:choose>

						</fo:table-cell>

					</fo:table-row>

				</xsl:for-each> 

			</fo:table-body>

		</fo:table>

  </xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE PHYSICAL OBJECT ENTITY (FOR THE RESEARCH WORK) -->	
<!--########################################################################-->

  <xsl:template name="research-work-physical-object">
    <xsl:param name="entity"/>

    <!-- entity name -->
		<xsl:call-template name="value-single">
			<xsl:with-param name="value" select="$lang.research-work.physical-object"/>
			<xsl:with-param name="fontSize" select="$font.size.standard"/>
			<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
			<xsl:with-param name="addRuler" select="'false'"/>
		</xsl:call-template>

		<!-- table-->
		<fo:table table-layout="fixed" 
							vertical-align="middle"
							border-before-style="hidden" 
							border-after-style="hidden"
							border-start-style="hidden"
							border-end-style="hidden">

			<!-- define the table columns -->
			<fo:table-column column-width="proportional-column-width(1)"/>	<!-- this construct is used to align the table in a centred way	-->						
			<fo:table-column column-width="40.0%"/>
			<fo:table-column column-width="60.0%"/>
			<fo:table-column column-width="proportional-column-width(1)"/>  <!-- this is also part of central alignment approach --> 

			<!-- table body (table header is omitted here) -->
 		  <fo:table-body>	

				<xsl:for-each select="$entity">	

					<fo:table-row>

						<!-- left column -->
						<fo:table-cell column-number="2"
													 border-width="{$width.border}"
													 border-color="{$colour.border}"
													 border-style="{$style.border}"
													 border-start-style="hidden"
													 margin-left="{$margin.left.table-cell}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="cerif:Title"/>
							</xsl:call-template>	

						</fo:table-cell>

						<!-- right column -->
						<fo:table-cell column-number="3"
													 border-width="{$width.border}"
													 border-color="{$colour.border}"
													 border-style="{$style.border}"
													 border-end-style="hidden"
													 margin-left="{$margin.left.table-cell}">

							<!-- this approach is necessary to avoid problems with empty metadata --> 
							<xsl:choose>
                <xsl:when test="cerif:Description or cerif:Type or cerif:Result/cerif:Function or cerif:Result/cerif:ContactInformation or cerif:Result/cerif:Accessibility or cerif:Result/cerif:WebAddress">

                  <!-- if the space above a label-value pair is reduced generally, there are problems for the first entry --> 
									<xsl:call-template name="vertical-gap">
									  <xsl:with-param name="label" select="$margin.top.label-table-start"/>
									</xsl:call-template>

									<xsl:call-template name="label-value-single-below-table"> 
										<xsl:with-param name="label" select="$lang.description"/>
										<xsl:with-param name="value" select="cerif:Description"/>
									</xsl:call-template>

									<xsl:call-template name="label-value-single-below-table"> 
										<xsl:with-param name="label" select="$lang.type"/>
										<xsl:with-param name="value" select="cerif:Type"/>
									</xsl:call-template>

									<xsl:call-template name="label-value-comma-list-below-table"> 
										<xsl:with-param name="label" select="$lang.physical-object.function"/>
										<xsl:with-param name="value" select="cerif:Result/cerif:Function"/>
									</xsl:call-template>

									<xsl:call-template name="label-value-single-below-table"> 
										<xsl:with-param name="label" select="$lang.physical-object.accessibility"/>
										<xsl:with-param name="value" select="cerif:Result/cerif:Accessibility"/>
									</xsl:call-template>

									<xsl:call-template name="label-value-single-below-table"> 
										<xsl:with-param name="label" select="$lang.physical-object.contact-information"/>
										<xsl:with-param name="value" select="cerif:Result/cerif:ContactInformation"/>
									</xsl:call-template>

									<xsl:call-template name="label-value-single-below-table"> 
										<xsl:with-param name="label" select="$lang.web-address"/>
										<xsl:with-param name="value" select="cerif:Result/cerif:WebAddress"/>
									</xsl:call-template>

	 							</xsl:when>
  						  <xsl:otherwise>
									<xsl:call-template name="empty-table-cell-catcher"/>
						    </xsl:otherwise>
							</xsl:choose>

						</fo:table-cell>

					</fo:table-row>

				</xsl:for-each> 

			</fo:table-body>

		</fo:table>

  </xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE PUBLICATION ENTITY -->	
<!--########################################################################-->

  <xsl:template name="publication">

	  <xsl:if test="cerif:Publication">
	
			<xsl:for-each select="cerif:Publication/cerif:Index">

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
						<xsl:with-param name="addRuler" select="'false'"/>
					</xsl:call-template>	

          <!-- create table or not (simple check based on type and author, this makes sure that left and right some content exists) -->
          <xsl:if test="cerif:Type or cerif:Author">
            <xsl:call-template name="ruler"/>

						<fo:table table-layout="fixed" 
						          vertical-align="middle"
											border-before-style="hidden" 
											border-after-style="hidden"
											border-start-style="hidden"
											border-end-style="hidden">
				
							<!-- define the table columns -->
							<fo:table-column column-width="proportional-column-width(1)"/>	this construct is used to align the table in a centred way							
							<fo:table-column column-width="60.0%"/>
							<fo:table-column column-width="40.0%"/>
							<fo:table-column column-width="proportional-column-width(1)"/>  <!-- this is also part of central alignment approach --> 
																																							
						  <!-- table body (table header is omitted in this case)-->
							<fo:table-body>					
								<fo:table-row>

									<!-- left column -->
									<fo:table-cell column-number="2"
																 border-width="{$width.border}"
																 border-color="{$colour.border}"
																 border-style="{$style.border}"
																 border-start-style="hidden"
																 border-end-style="hidden"
																 margin-left="{$margin.left.table-cell}">

										<xsl:call-template name="value-issue-date-author-table"> 
											<xsl:with-param name="issueDate" select="cerif:Date"/>
											<xsl:with-param name="author" select="cerif:Author"/>
										</xsl:call-template>	

										<xsl:call-template name="value-publication-information-table"> 
											<xsl:with-param name="isPartOf" select="cerif:IsPartOf"/>
											<xsl:with-param name="journal" select="cerif:Journal"/>
											<xsl:with-param name="volume" select="cerif:Volume"/>
											<xsl:with-param name="issue" select="cerif:Issue"/>
											<xsl:with-param name="startPage" select="cerif:StartPage"/>
											<xsl:with-param name="endPage" select="cerif:EndPage"/>
											<xsl:with-param name="identifier" select="cerif:Identifier"/>
										</xsl:call-template>	

									</fo:table-cell>

									<!-- right column -->
									<fo:table-cell column-number="3"
																	border-width="{$width.border}"
																	border-color="{$colour.border}"
																	border-style="{$style.border}"
																	border-start-style="hidden"
																	border-end-style="hidden">

										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:Type"/>
										</xsl:call-template>	

										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:Reference/cerif:TargetAudience"/>
										</xsl:call-template>	

									</fo:table-cell>

								</fo:table-row>

							</fo:table-body>	

						</fo:table>

					</xsl:if>  

          <!-- abstract -->
					<xsl:call-template name="label-value-single-below">	
						<xsl:with-param name="label" select="$lang.abstract"/>
						<xsl:with-param name="value" select="cerif:Abstract"/>
					</xsl:call-template>

				</fo:block>

		  </xsl:for-each> 
	
		</xsl:if>

	</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE PRODUCT ENTITY -->	
<!--########################################################################-->

  <xsl:template name="product">

	  <xsl:if test="cerif:Product">
	
			<xsl:for-each select="cerif:Product/cerif:Index">

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
						<xsl:with-param name="addRuler" select="'false'"/>
					</xsl:call-template>		

				  <xsl:call-template name="label-author-affilation-comma-list-below">
						<xsl:with-param name="label" select="$lang.author"/>
						<xsl:with-param name="value" select="cerif:Contributor/cerif:Index"/>
				  </xsl:call-template>

					<xsl:call-template name="label-value-single-below">	
						<xsl:with-param name="label" select="$lang.product.publication-date"/>
						<xsl:with-param name="value" select="cerif:PublicationDate"/>
					</xsl:call-template>

					<xsl:call-template name="label-value-comma-list-below">	
						<xsl:with-param name="label" select="$lang.product.repository"/>
						<xsl:with-param name="value" select="cerif:Publisher"/>
					</xsl:call-template>

					<xsl:call-template name="label-value-single-below">	
						<xsl:with-param name="label" select="$lang.language"/>
						<xsl:with-param name="value" select="cerif:Language"/>
					</xsl:call-template>

					<xsl:call-template name="label-value-single-below">	
						<xsl:with-param name="label" select="$lang.abstract"/>
						<xsl:with-param name="value" select="cerif:Abstract"/>
					</xsl:call-template>

					<xsl:call-template name="label-value-comma-list-below">	
						<xsl:with-param name="label" select="$lang.keyword"/>
						<xsl:with-param name="value" select="cerif:Keyword"/>
					</xsl:call-template>

					<xsl:call-template name="label-value-single-below">	
						<xsl:with-param name="label" select="$lang.identifier"/>
						<xsl:with-param name="value" select="cerif:Identifier"/>
					</xsl:call-template>

					<xsl:call-template name="label-value-line-list-below">	
						<xsl:with-param name="label" select="$lang.product.reference"/>
						<xsl:with-param name="value" select="cerif:Reference"/>
					</xsl:call-template>

<!--TODO: references-->

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

						<xsl:call-template name="value-icon-count-table"> 
							<xsl:with-param name="iconFileName" select="'link.png'"/>
							<xsl:with-param name="count" select="count(cerif:InnovationPotential/cerif:Index)"/>
						</xsl:call-template>	

					</fo:table-cell>

					<!-- innovation idea -->
					<fo:table-cell column-number="3" 
													border-width="{$width.border}"
													border-color="{$colour.border}"
													border-style="{$style.border}">

						<xsl:call-template name="value-icon-count-table"> 
							<xsl:with-param name="iconFileName" select="'process.png'"/>
							<xsl:with-param name="count" select="count(cerif:InnovationIdea/cerif:Index)"/>
						</xsl:call-template>	

					</fo:table-cell>

					<!-- application -->
					<fo:table-cell column-number="4" 
													border-width="{$width.border}"
													border-color="{$colour.border}"
													border-style="{$style.border}">

						<xsl:call-template name="value-icon-count-table"> 
							<xsl:with-param name="iconFileName" select="'object-material.png'"/>
							<xsl:with-param name="count" select="count(cerif:Application/cerif:Index)"/>
						</xsl:call-template>	

					</fo:table-cell>

					<!-- unexpected result -->
					<fo:table-cell column-number="5" 
													border-width="{$width.border}"
													border-color="{$colour.border}"
													border-style="{$style.border}">

						<xsl:call-template name="value-icon-count-table"> 
							<xsl:with-param name="iconFileName" select="'task.png'"/>
							<xsl:with-param name="count" select="count(cerif:UnexpectedResult/cerif:Index)"/>
						</xsl:call-template>	

					</fo:table-cell>

					<!-- patent -->
					<fo:table-cell column-number="6" 
													border-width="{$width.border}"
													border-color="{$colour.border}"
													border-style="{$style.border}">

						<xsl:call-template name="value-icon-count-table"> 
							<xsl:with-param name="iconFileName" select="'publication.png'"/>
							<xsl:with-param name="count" select="count(cerif:Patent/cerif:Index)"/>
						</xsl:call-template>	

					</fo:table-cell>

					<!-- spinoff -->
					<fo:table-cell column-number="7" 
													border-width="{$width.border}"
													border-color="{$colour.border}"
													border-style="{$style.border}">

						<xsl:call-template name="value-icon-count-table"> 
							<xsl:with-param name="iconFileName" select="'work-package.png'"/>
							<xsl:with-param name="count" select="count(cerif:Spinoff/cerif:Index)"/>
						</xsl:call-template>	

					</fo:table-cell>

					<!-- further steps -->
					<fo:table-cell column-number="8" 
													border-width="{$width.border}"
													border-color="{$colour.border}"
													border-style="{$style.border}">

						<xsl:call-template name="value-icon-count-table"> 
							<xsl:with-param name="iconFileName" select="'object-material.png'"/>
							<xsl:with-param name="count" select="count(cerif:Step/cerif:Index)"/>
						</xsl:call-template>	

					</fo:table-cell>

					<!-- award -->
					<fo:table-cell column-number="9" 
													border-width="{$width.border}"
													border-color="{$colour.border}"
													border-style="{$style.border}">

						<xsl:call-template name="value-icon-count-table"> 
							<xsl:with-param name="iconFileName" select="'publication.png'"/>
							<xsl:with-param name="count" select="count(cerif:Award/cerif:Index)"/>
						</xsl:call-template>	

					</fo:table-cell>

					<!-- open research question -->
					<fo:table-cell column-number="10" 
													border-width="{$width.border}"
													border-color="{$colour.border}"
													border-style="{$style.border}">

						<xsl:call-template name="value-icon-count-table"> 
							<xsl:with-param name="iconFileName" select="'indirect-target-group.png'"/>
							<xsl:with-param name="count" select="count(cerif:OpenResearchQuestion/cerif:Index)"/>
						</xsl:call-template>													

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

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.overview.innovation-potential"/>
								<xsl:with-param name="alignment" select="'center'"/>
							</xsl:call-template>	

						</fo:table-cell>

						<!-- innovation idea -->
						<fo:table-cell column-number="3"
														border-width="{$width.border}"
														border-color="{$colour.border}"
														border-style="{$style.border}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.overview.innovation-idea"/>
								<xsl:with-param name="alignment" select="'center'"/>
							</xsl:call-template>	

						</fo:table-cell>

						<!-- application -->
						<fo:table-cell column-number="4"
														border-width="{$width.border}"
														border-color="{$colour.border}"
														border-style="{$style.border}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.overview.application"/>
								<xsl:with-param name="alignment" select="'center'"/>
							</xsl:call-template>	

						</fo:table-cell>

						<!-- unexpected result -->
						<fo:table-cell column-number="5"
														border-width="{$width.border}"
														border-color="{$colour.border}"
														border-style="{$style.border}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.overview.unexpected-result"/>
								<xsl:with-param name="alignment" select="'center'"/>
							</xsl:call-template>	

						</fo:table-cell>

						<!-- patent -->
						<fo:table-cell column-number="6"
														border-width="{$width.border}"
														border-color="{$colour.border}"
														border-style="{$style.border}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.overview.patent"/>
								<xsl:with-param name="alignment" select="'center'"/>
							</xsl:call-template>	

						</fo:table-cell>

						<!-- spinoff -->
						<fo:table-cell column-number="7"
														border-width="{$width.border}"
														border-color="{$colour.border}"
														border-style="{$style.border}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.overview.spinoff"/>
								<xsl:with-param name="alignment" select="'center'"/>
							</xsl:call-template>			

						</fo:table-cell>

						<!-- further steps -->
						<fo:table-cell column-number="8"
														border-width="{$width.border}"
														border-color="{$colour.border}"
														border-style="{$style.border}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.overview.further-steps"/>
								<xsl:with-param name="alignment" select="'center'"/>
							</xsl:call-template>	

						</fo:table-cell>

						<!-- award -->
						<fo:table-cell column-number="9"
														border-width="{$width.border}"
														border-color="{$colour.border}"
														border-style="{$style.border}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.overview.award"/>
								<xsl:with-param name="alignment" select="'center'"/>
							</xsl:call-template>	

						</fo:table-cell>

						<!-- open research question -->
						<fo:table-cell column-number="10"
														border-width="{$width.border}"
														border-color="{$colour.border}"
														border-style="{$style.border}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.overview.open-research-question"/>
								<xsl:with-param name="alignment" select="'center'"/>
							</xsl:call-template>	

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
						<xsl:with-param name="fontSize" select="$font.size.key-value"/>
						<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
						<xsl:with-param name="addRuler" select="'false'"/>
					</xsl:call-template>									

					<xsl:call-template name="label-value-single-below">	
						<xsl:with-param name="label" select="$lang.type"/>
						<xsl:with-param name="value" select="cerif:Type"/>
				  </xsl:call-template>

					<xsl:call-template name="label-value-line-list-below">	
						<xsl:with-param name="label" select="$lang.innovation-potential.target-group"/>
						<xsl:with-param name="value" select="cerif:TargetGroup"/>
				  </xsl:call-template>

				 	<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.innovation-potential.description"/>
						<xsl:with-param name="value" select="cerif:Description"/>
				  </xsl:call-template> 

				 	<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.innovation-potential.applicability-novelty"/>
						<xsl:with-param name="value" select="cerif:Applicability/cerif:Novelty"/>
				  </xsl:call-template> 

				 	<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.innovation-potential.applicability-efficiency"/>
						<xsl:with-param name="value" select="cerif:Applicability/cerif:Efficiency"/>
				  </xsl:call-template>

				 	<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.innovation-potential.applicability-practicability"/>
						<xsl:with-param name="value" select="cerif:Applicability/cerif:Practicability"/>
				  </xsl:call-template>

				 	<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.innovation-potential.applicability-requirements"/>
						<xsl:with-param name="value" select="cerif:Applicability/cerif:Requirements"/>
				  </xsl:call-template>

			 	<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.innovation-potential.applicability-prospects"/>
						<xsl:with-param name="value" select="cerif:Applicability/cerif:Prospects"/>
				  </xsl:call-template>

			 	<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.innovation-potential.regionalscope-primaryfield"/>
						<xsl:with-param name="value" select="cerif:RegionalScope/cerif:PrimaryField"/>
				  </xsl:call-template>

			 	<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.innovation-potential.regionalscope-laterdissimination"/>
						<xsl:with-param name="value" select="cerif:RegionalScope/cerif:LaterDissimination"/>
				  </xsl:call-template>

			 	<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.innovation-potential.regionalscope-description"/>
						<xsl:with-param name="value" select="cerif:RegionalScope/cerif:Description"/>
				  </xsl:call-template>

			 	<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.innovation-potential.srltrl-development-project-start"/>
						<xsl:with-param name="value" select="cerif:SRLTRL/cerif:DevelopmentProjectStart"/>
				  </xsl:call-template>

			 	<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.innovation-potential.srltrl-development-project-end"/>
						<xsl:with-param name="value" select="cerif:SRLTRL/cerif:DevelopmentProjectEnd"/>
				  </xsl:call-template>

			 	<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.innovation-potential.srltrl-selection"/>
						<xsl:with-param name="value" select="cerif:SRLTRL/cerif:Selection"/>
				  </xsl:call-template>

				  			 	<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.innovation-potential.srltrl-readiness-project-start"/>
						<xsl:with-param name="value" select="cerif:SRLTRL/cerif:ReadinessProjectStart"/>
				  </xsl:call-template>

				  			 	<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.innovation-potential.srltrl-readiness-project-end"/>
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
						<xsl:with-param name="fontSize" select="$font.size.key-value"/>
						<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
						<xsl:with-param name="addRuler" select="'false'"/>
					</xsl:call-template>									

					<xsl:call-template name="label-value-line-list-below">	
						<xsl:with-param name="label" select="$lang.type"/>
						<xsl:with-param name="value" select="cerif:Type"/>
				  </xsl:call-template>

				 	<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.innovationidea.description"/>
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
						<xsl:with-param name="fontSize" select="$font.size.key-value"/>
						<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
						<xsl:with-param name="addRuler" select="'false'"/>
					</xsl:call-template>									

					<xsl:call-template name="label-value-line-list-below">	
						<xsl:with-param name="label" select="$lang.application.related-innovation"/>
						<xsl:with-param name="value" select="cerif:RelatedInnovation"/>
				  </xsl:call-template>

				 	<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.unit"/>
						<xsl:with-param name="value" select="cerif:Unit"/>
				  </xsl:call-template> 

<!-- eingegebene eigene Einheiten werden im System nicht abgespeichert -->
				 	<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.application.free-unit"/>
						<xsl:with-param name="value" select="cerif:FreeUnit"/>
				  </xsl:call-template> 

					<fo:block space-before="12pt" space-after="12pt">
					<!-- This block represents an empty line with 12pt spacing above and below -->
					</fo:block>

						<fo:table table-layout="fixed" 
												vertical-align="middle"
												border-before-style="hidden" 
												border-after-style="hidden"
												border-start-style="hidden"
												border-end-style="hidden">
									
										<!-- define the table columns -->
					<!-- define the table columns -->
							<fo:table-column column-width="proportional-column-width(1)"/>	<!-- this construct is used to align the table in a centred way	-->						
							<fo:table-column column-width="20.0%"/>
							<fo:table-column column-width="20.0%"/>
							<fo:table-column column-width="30.0%"/>
							<fo:table-column column-width="30.0%"/>
							<fo:table-column column-width="proportional-column-width(1)"/> 					
							
							<fo:table-header>
							
								<fo:table-cell column-number="2"  					
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																border-start-style="hidden"
																margin-left="{$margin.left.table-cell}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.application.reference-year"/>
								<xsl:with-param name="fontSize" select="$font.size.key-value"/>
								<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
							</xsl:call-template>

									
								</fo:table-cell>

								<fo:table-cell column-number="3" 
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																margin-left="{$margin.left.table-cell}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.application.quantity"/>
								<xsl:with-param name="fontSize" select="$font.size.key-value"/>
								<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
							</xsl:call-template>

								</fo:table-cell>

								<fo:table-cell column-number="4" 
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																margin-left="{$margin.left.table-cell}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.application.regionaloutreach"/>
								<xsl:with-param name="fontSize" select="$font.size.key-value"/>
								<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
							</xsl:call-template>

								</fo:table-cell>

									<fo:table-cell column-number="5" 
																border-width="{$width.border}"
													            border-color="{$colour.border}"
																border-style="{$style.border}"
																border-end-style="hidden"
																margin-left="{$margin.left.table-cell}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.application.success-description"/>
								<xsl:with-param name="fontSize" select="$font.size.key-value"/>
								<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
							</xsl:call-template>

								</fo:table-cell>				

								
							</fo:table-header>

							<fo:table-body>
								<xsl:for-each select="cerif:SuccessQuantification/cerif:Index">
								
								<fo:table-row>

									<fo:table-cell column-number="2" 		
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																border-start-style="hidden"
																margin-left="{$margin.left.table-cell}">
																
										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:ReferenceYear"/>
										</xsl:call-template>

									</fo:table-cell>

									<fo:table-cell column-number="3" 		
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																margin-left="{$margin.left.table-cell}">
																
										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:Quantity"/>
										</xsl:call-template>

									</fo:table-cell>

									<fo:table-cell column-number="4" 		
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																margin-left="{$margin.left.table-cell}">
																
										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:RegionalOutreach"/>
										</xsl:call-template>

									</fo:table-cell>
		
									<fo:table-cell column-number="5" 		
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																border-end-style="hidden"
																margin-left="{$margin.left.table-cell}">
																
										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:SuccessDescription"/>
										</xsl:call-template>

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
						<xsl:with-param name="fontSize" select="$font.size.key-value"/>
						<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
						<xsl:with-param name="addRuler" select="'false'"/>
					</xsl:call-template>									

					<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.unexpected-result.result-description"/>
						<xsl:with-param name="value" select="cerif:ResultDescription"/>
				  </xsl:call-template>

				 	<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.unexpected-result.status-publication"/>
						<xsl:with-param name="value" select="cerif:Publication/cerif:Index/cerif:StatusPublication"/>
				  </xsl:call-template> 

					<xsl:call-template name="label-value-line-list-below">				
						<xsl:with-param name="label" select="$lang.unexpected-result.link-publication"/>
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
						<xsl:with-param name="fontSize" select="$font.size.key-value"/>
						<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
						<xsl:with-param name="addRuler" select="'false'"/>
					</xsl:call-template>									

					<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.type"/>
						<xsl:with-param name="value" select="cerif:Type"/>
				  </xsl:call-template>						

					<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.patent.use"/>
						<xsl:with-param name="value" select="cerif:Use"/>
				  </xsl:call-template>						

					<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.patent.use-description"/>
						<xsl:with-param name="value" select="cerif:UseDescription"/>
				  </xsl:call-template>	

          <xsl:if test="cerif:Result/cerif:Holder">
						<xsl:call-template name="vertical-gap"/>
          </xsl:if>

					<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.patent.registration-number"/>
						<xsl:with-param name="value" select="cerif:Result/cerif:RegistrationNumber"/>
				  </xsl:call-template>	

					<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.patent.registration-date"/>
						<xsl:with-param name="value" select="cerif:Result/cerif:RegistrationDate"/>
				  </xsl:call-template>	

					<xsl:call-template name="label-value-comma-list-below"> 
						<xsl:with-param name="label" select="$lang.patent.holder"/>
						<xsl:with-param name="value" select="cerif:Result/cerif:Holder"/>
				  </xsl:call-template>	

          <xsl:if test="cerif:Contributor/cerif:Index">
						<xsl:call-template name="vertical-gap"/>
          </xsl:if>

					<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.patent.patent-number"/>
						<xsl:with-param name="value" select="cerif:Import/cerif:PatentNumber"/>
				  </xsl:call-template>	

					<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.patent.approval-date"/>
						<xsl:with-param name="value" select="cerif:Import/cerif:ApprovalDate"/>
				  </xsl:call-template>	

					<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.patent.publication-date"/>
						<xsl:with-param name="value" select="cerif:Import/cerif:PublicationDate"/>
				  </xsl:call-template>	

					<xsl:call-template name="label-author-affilation-comma-list-below">
						<xsl:with-param name="label" select="$lang.patent.inventor"/>
						<xsl:with-param name="value" select="cerif:Contributor/cerif:Index"/>
				  </xsl:call-template>	

					<xsl:call-template name="label-value-comma-list-below"> 
						<xsl:with-param name="label" select="$lang.patent.issuer"/>
						<xsl:with-param name="value" select="cerif:Import/cerif:Issuer"/>
				  </xsl:call-template>	

					<xsl:call-template name="label-value-comma-list-below"> 
						<xsl:with-param name="label" select="$lang.patent.ipc-class"/>
						<xsl:with-param name="value" select="cerif:Import/cerif:IPCClass"/>
				  </xsl:call-template>	

					<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="concat($lang.language,' ',$lang.abstract)"/>
						<xsl:with-param name="value" select="cerif:Import/cerif:Language"/>
				  </xsl:call-template>	

					<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.abstract"/>
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
					<xsl:with-param name="fontSize" select="$font.size.key-value"/>
					<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
					<xsl:with-param name="addRuler" select="'false'"/>
				</xsl:call-template>	

				<xsl:call-template name="label-value-single-below">
					<xsl:with-param name="label" select="$lang.description"/>
					<xsl:with-param name="value" select="cerif:Description"/>
				</xsl:call-template>

				<xsl:call-template name="label-value-single-below">
					<xsl:with-param name="label" select="$lang.spinoff.result-registercourt"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:RegisterCourt"/>
				</xsl:call-template>

				<xsl:call-template name="label-value-single-below">
					<xsl:with-param name="label" select="$lang.spinoff.result-registertype"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:RegisterType"/>
				</xsl:call-template>

				<xsl:call-template name="label-value-single-below">
					<xsl:with-param name="label" select="$lang.spinoff.result-registernumber"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:RegisterNumber"/>
				</xsl:call-template>

				<xsl:call-template name="label-value-single-below">
					<xsl:with-param name="label" select="$lang.spinoff.result-foundingdate"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:FoundingDate"/>
				</xsl:call-template>

				<xsl:call-template name="label-value-single-below">
					<xsl:with-param name="label" select="$lang.spinoff.result-type"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:Type"/>
				</xsl:call-template>

				<xsl:call-template name="label-value-single-below">
					<xsl:with-param name="label" select="$lang.spinoff.result-nace"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:NACE"/>
				</xsl:call-template>

				<xsl:call-template name="label-value-single-below">
					<xsl:with-param name="label" select="$lang.street"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:Street"/>
				</xsl:call-template>

				<xsl:call-template name="label-value-single-below">
					<xsl:with-param name="label" select="$lang.postcode"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:PostCode"/>
				</xsl:call-template>

				<xsl:call-template name="label-value-single-below">
					<xsl:with-param name="label" select="$lang.city"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:City"/>
				</xsl:call-template>

				<xsl:call-template name="label-value-single-below">
					<xsl:with-param name="label" select="$lang.country"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:Country"/>
				</xsl:call-template>

				<xsl:call-template name="label-value-single-below">
					<xsl:with-param name="label" select="$lang.spinoff.result-webaddress"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:WebAddress"/>
				</xsl:call-template>

				<xsl:call-template name="label-value-line-list-below">				
					<xsl:with-param name="label" select="$lang.spinoff.result-relatedorganisation"/>
					<xsl:with-param name="value" select="cerif:Result/cerif:RelatedOrganisation"/>
				</xsl:call-template>

				<xsl:call-template name="title">
					<xsl:with-param name="title" select="$lang.spinoff.funding-sectiontitle"/>
					<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
				</xsl:call-template>

				<fo:block space-before="2pt" space-after="2pt">
				<!-- This block represents an empty line with 1 pt -->
				</fo:block>

					<fo:table table-layout="fixed" 
												vertical-align="middle"
												border-before-style="hidden" 
												border-after-style="hidden"
												border-start-style="hidden"
												border-end-style="hidden">

							<fo:table-column column-width="proportional-column-width(1)"/>	<!-- this construct is used to align the table in a centred way	-->						
							<fo:table-column column-width="10.0%"/>
							<fo:table-column column-width="25.0%"/>
							<fo:table-column column-width="20.0%"/>
							<fo:table-column column-width="15.0%"/>
							<fo:table-column column-width="30.0%"/>					
							<fo:table-column column-width="proportional-column-width(1)"/> 	

							<fo:table-header>
								
								<fo:table-cell column-number="2"  					
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																border-start-style="hidden"
																margin-left="{$margin.left.table-cell}">

												<xsl:call-template name="value-single-table"> 
													<xsl:with-param name="value" select="$lang.year"/>
													<xsl:with-param name="fontSize" select="$font.size.key-value"/>
													<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
												</xsl:call-template>
									
								</fo:table-cell>


								<fo:table-cell column-number="3" 
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																margin-left="{$margin.left.table-cell}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.spinoff.funding-title"/>
								<xsl:with-param name="fontSize" select="$font.size.key-value"/>
								<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
							</xsl:call-template>

								</fo:table-cell>

								<fo:table-cell column-number="4" 
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																margin-left="{$margin.left.table-cell}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.spinoff.funding-amount"/>
								<xsl:with-param name="fontSize" select="$font.size.key-value"/>
								<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
							</xsl:call-template>

								</fo:table-cell>

									<fo:table-cell column-number="5" 
																border-width="{$width.border}"
													            border-color="{$colour.border}"
																border-style="{$style.border}"
																margin-left="{$margin.left.table-cell}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.currency"/>
								<xsl:with-param name="fontSize" select="$font.size.key-value"/>
								<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
							</xsl:call-template>

								</fo:table-cell>				

									<fo:table-cell column-number="6" 
																border-width="{$width.border}"
													            border-color="{$colour.border}"
																border-style="{$style.border}"
																border-end-style="hidden"
																margin-left="{$margin.left.table-cell}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.spinoff.funding-description"/>
								<xsl:with-param name="fontSize" select="$font.size.key-value"/>
								<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
							</xsl:call-template>

								</fo:table-cell>
							
							</fo:table-header>


							<fo:table-body>
								<xsl:for-each select="cerif:Funding/cerif:Index">
								
								<fo:table-row>

									<fo:table-cell column-number="2" 		
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																border-start-style="hidden"
																margin-left="{$margin.left.table-cell}">
																
										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:Year"/>
										</xsl:call-template>
									</fo:table-cell>

									<fo:table-cell column-number="3" 		
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																margin-left="{$margin.left.table-cell}">
																
										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:Title"/>
										</xsl:call-template>

									</fo:table-cell>

									<fo:table-cell column-number="4" 		
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																margin-left="{$margin.left.table-cell}">
																
										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:Amount"/>
										</xsl:call-template>

									</fo:table-cell>
		
									<fo:table-cell column-number="5" 		
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																margin-left="{$margin.left.table-cell}">
																
										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:Currency"/>
										</xsl:call-template>

									</fo:table-cell>

									<fo:table-cell column-number="6" 		
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																border-end-style="hidden"
																margin-left="{$margin.left.table-cell}">
																
										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:Description"/>
										</xsl:call-template>

									</fo:table-cell>		

								</fo:table-row>
								</xsl:for-each>
							</fo:table-body>
							</fo:table>

			<xsl:call-template name="title">
				<xsl:with-param name="title" select="$lang.spinoff.performance-title"/>
				<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
			</xsl:call-template>

				<fo:block space-before="2pt" space-after="2pt">
				<!-- This block represents an empty line with 2 pt  -->
				</fo:block>

					<fo:table table-layout="fixed" 
												vertical-align="middle"
												border-before-style="hidden" 
												border-after-style="hidden"
												border-start-style="hidden"
												border-end-style="hidden">

							<fo:table-column column-width="proportional-column-width(1)"/>	<!-- this construct is used to align the table in a centred way	-->						
							<fo:table-column column-width="33.3%"/>
							<fo:table-column column-width="33.3%"/>
							<fo:table-column column-width="33.3%"/>			
							<fo:table-column column-width="proportional-column-width(1)"/> 	

							<fo:table-header>
								
								<fo:table-cell column-number="2"  					
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																border-start-style="hidden"
																margin-left="{$margin.left.table-cell}">

												<xsl:call-template name="value-single-table"> 
													<xsl:with-param name="value" select="$lang.year"/>
													<xsl:with-param name="fontSize" select="$font.size.key-value"/>
													<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
												</xsl:call-template>
									
								</fo:table-cell>


								<fo:table-cell column-number="3" 
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																margin-left="{$margin.left.table-cell}">

												<xsl:call-template name="value-single-table"> 
													<xsl:with-param name="value" select="$lang.spinoff.performance-amount"/>
													<xsl:with-param name="fontSize" select="$font.size.key-value"/>
													<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
												</xsl:call-template>

								</fo:table-cell>

									<fo:table-cell column-number="4" 
																border-width="{$width.border}"
													            border-color="{$colour.border}"
																border-style="{$style.border}"
																border-end-style="hidden"
																margin-left="{$margin.left.table-cell}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.currency"/>
								<xsl:with-param name="fontSize" select="$font.size.key-value"/>
								<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
							</xsl:call-template>

								</fo:table-cell>
							
							</fo:table-header>


							<fo:table-body>
								<xsl:for-each select="cerif:Performance/cerif:Index">
								
								<fo:table-row>

									<fo:table-cell column-number="2" 		
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																border-start-style="hidden"
																margin-left="{$margin.left.table-cell}">
																
										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:Year"/>
										</xsl:call-template>
									</fo:table-cell>
		
									<fo:table-cell column-number="3" 		
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																margin-left="{$margin.left.table-cell}">
																
										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:Amount"/>
										</xsl:call-template>

									</fo:table-cell>

									<fo:table-cell column-number="4" 		
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																border-end-style="hidden"
																margin-left="{$margin.left.table-cell}">
																
										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:Currency"/>
										</xsl:call-template>

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
						<xsl:with-param name="fontSize" select="$font.size.key-value"/>
						<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
						<xsl:with-param name="addRuler" select="'false'"/>
					</xsl:call-template>

					<xsl:call-template name="label-value-line-list-below">
						<xsl:with-param name="label" select="$lang.step.partner"/>
						<xsl:with-param name="value" select="cerif:Partner"/>
				  </xsl:call-template>

					<xsl:call-template name="label-value-line-list-below">
						<xsl:with-param name="label" select="$lang.step.target-group"/>
						<xsl:with-param name="value" select="cerif:TargetGroup"/>
				  </xsl:call-template>

					<xsl:call-template name="label-value-line-list-below">
						<xsl:with-param name="label" select="$lang.step.type"/>
						<xsl:with-param name="value" select="cerif:Type"/>
				  </xsl:call-template>

					<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.step.date"/>
						<xsl:with-param name="value" select="cerif:Date"/>
				  </xsl:call-template>

					<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.description"/>
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
					<xsl:with-param name="fontSize" select="$font.size.key-value"/>
					<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
					<xsl:with-param name="addRuler" select="'false'"/>
				</xsl:call-template>	

				<xsl:call-template name="label-value-single-below">
					<xsl:with-param name="label" select="$lang.description"/>
					<xsl:with-param name="value" select="cerif:Description"/>
				</xsl:call-template>

				<xsl:call-template name="label-value-single-below">
					<xsl:with-param name="label" select="$lang.award.date"/>
					<xsl:with-param name="value" select="cerif:Date"/>
				</xsl:call-template>

				<xsl:call-template name="label-amount-currency">
					<xsl:with-param name="label" select="$lang.award.pricemoney"/>
					<xsl:with-param name="amount" select="cerif:PriceMoney"/>
					<xsl:with-param name="currency" select="cerif:Currency"/>
				</xsl:call-template>

				<xsl:call-template name="label-value-single-below">
					<xsl:with-param name="label" select="$lang.currency"/>
					<xsl:with-param name="value" select="cerif:Currency"/>
				</xsl:call-template>

				<xsl:call-template name="label-value-comma-list-below">
					<xsl:with-param name="label" select="$lang.award.winner"/>
					<xsl:with-param name="value" select="cerif:Winner"/>
				</xsl:call-template>

				<xsl:call-template name="label-value-comma-list-below">
					<xsl:with-param name="label" select="$lang.award.organisation"/>
					<xsl:with-param name="value" select="cerif:Organisation"/>
				</xsl:call-template>

				<xsl:call-template name="label-value-comma-list-below">
					<xsl:with-param name="label" select="$lang.award.donor"/>
					<xsl:with-param name="value" select="cerif:Donor"/>
				</xsl:call-template>

				<xsl:call-template name="label-value-single-below">
					<xsl:with-param name="label" select="$lang.award.target-group"/>
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
					<xsl:with-param name="fontSize" select="$font.size.key-value"/>
					<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
					<xsl:with-param name="addRuler" select="'false'"/>
				</xsl:call-template>	

				<xsl:call-template name="label-value-single-below">
					<xsl:with-param name="label" select="$lang.condition.conditiontype"/>
					<xsl:with-param name="value" select="cerif:ConditionType"/>
				</xsl:call-template>

				<xsl:call-template name="label-value-single-below">
					<xsl:with-param name="label" select="$lang.condition.influence"/>
					<xsl:with-param name="value" select="cerif:Influence"/>
				</xsl:call-template>

				<xsl:call-template name="label-value-single-below">
					<xsl:with-param name="label" select="$lang.condition.startframework"/>
					<xsl:with-param name="value" select="cerif:StartFramework"/>
				</xsl:call-template>

				<xsl:call-template name="label-value-single-below">
					<xsl:with-param name="label" select="$lang.description"/>
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
					<xsl:with-param name="addRuler" select="'false'"/>
				</xsl:call-template>	

				<xsl:call-template name="label-value-single-below">
					<xsl:with-param name="label" select="$lang.description"/>
					<xsl:with-param name="value" select="cerif:Description"/>
			  </xsl:call-template>

				<xsl:call-template name="label-value-single-below">
					<xsl:with-param name="label" select="$lang.expected-societal-impact.transitionarea"/>
					<xsl:with-param name="value" select="cerif:TransitionArea"/>
				</xsl:call-template>

				<xsl:call-template name="label-value-single-below">
					<xsl:with-param name="label" select="$lang.expected-societal-impact.evidence"/>
					<xsl:with-param name="value" select="cerif:Evidence"/>
				</xsl:call-template>

				<xsl:call-template name="title">
					<xsl:with-param name="title" select="$lang.expected-societal-impact.type"/>
					<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
				</xsl:call-template>

			<fo:block space-before="2pt" space-after="2pt">
				<!-- This block represents an empty line with 2 pt  -->
				</fo:block>

					<fo:table table-layout="fixed" 
												vertical-align="middle"
												border-before-style="hidden" 
												border-after-style="hidden"
												border-start-style="hidden"
												border-end-style="hidden">

							<fo:table-column column-width="proportional-column-width(1)"/>	<!-- this construct is used to align the table in a centred way	-->						
							<fo:table-column column-width="30.0%"/>
							<fo:table-column column-width="20.0%"/>
							<fo:table-column column-width="50.0%"/>			
							<fo:table-column column-width="proportional-column-width(1)"/> 	

							<fo:table-header>
								
								<fo:table-cell column-number="2"  					
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																border-start-style="hidden"
																margin-left="{$margin.left.table-cell}">

												<xsl:call-template name="value-single-table"> 
													<xsl:with-param name="value" select="$lang.expected-societal-impact.type-coreelement"/>
													<xsl:with-param name="fontSize" select="$font.size.key-value"/>
													<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
												</xsl:call-template>
									
								</fo:table-cell>


								<fo:table-cell column-number="3" 
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																margin-left="{$margin.left.table-cell}">

												<xsl:call-template name="value-single-table"> 
													<xsl:with-param name="value" select="$lang.expected-societal-impact.type-intensity"/>
													<xsl:with-param name="fontSize" select="$font.size.key-value"/>
													<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
												</xsl:call-template>

								</fo:table-cell>

									<fo:table-cell column-number="4" 
																border-width="{$width.border}"
													            border-color="{$colour.border}"
																border-style="{$style.border}"
																border-end-style="hidden"
																margin-left="{$margin.left.table-cell}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.expected-societal-impact.type-description"/>
								<xsl:with-param name="fontSize" select="$font.size.key-value"/>
								<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
							</xsl:call-template>

								</fo:table-cell>
							
							</fo:table-header>


							<fo:table-body>
								<xsl:for-each select="cerif:Type/cerif:Index">
								
								<fo:table-row>

									<fo:table-cell column-number="2" 		
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																border-start-style="hidden"
																margin-left="{$margin.left.table-cell}">
																
										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:CoreElement"/>
										</xsl:call-template>
									</fo:table-cell>
		
									<fo:table-cell column-number="3" 		
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																margin-left="{$margin.left.table-cell}">
																
										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:Intensity"/>
										</xsl:call-template>

									</fo:table-cell>

									<fo:table-cell column-number="4" 		
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																border-end-style="hidden"
																margin-left="{$margin.left.table-cell}">
																
										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:Description"/>
										</xsl:call-template>

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
					<xsl:with-param name="addRuler" select="'false'"/>
				</xsl:call-template>	

				<xsl:call-template name="label-value-single-below">
				<xsl:with-param name="label" select="$lang.description"/>
				<xsl:with-param name="value" select="cerif:Description"/>
			</xsl:call-template>

				<xsl:call-template name="label-value-single-below">
				<xsl:with-param name="label" select="$lang.negative-side-effect.transitionArea"/>
				<xsl:with-param name="value" select="cerif:TransitionArea"/>
			</xsl:call-template>

			<xsl:call-template name="label-value-single-below">
				<xsl:with-param name="label" select="$lang.negative-side-effect.evidence"/>
				<xsl:with-param name="value" select="cerif:Evidence"/>
			</xsl:call-template>

			<xsl:call-template name="title">
				<xsl:with-param name="title" select="$lang.negative-side-effect.type"/>
				<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
			</xsl:call-template>

			<fo:block space-before="2pt" space-after="2pt">
				<!-- This block represents an empty line with 2 pt  -->
				</fo:block>

					<fo:table table-layout="fixed" 
												vertical-align="middle"
												border-before-style="hidden" 
												border-after-style="hidden"
												border-start-style="hidden"
												border-end-style="hidden">

							<fo:table-column column-width="proportional-column-width(1)"/>	<!-- this construct is used to align the table in a centred way	-->						
							<fo:table-column column-width="40.0%"/>
							<fo:table-column column-width="20.0%"/>
							<fo:table-column column-width="40.0%"/>			
							<fo:table-column column-width="proportional-column-width(1)"/> 	

							<fo:table-header>
								
								<fo:table-cell column-number="2"  					
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																border-start-style="hidden"
																margin-left="{$margin.left.table-cell}">

												<xsl:call-template name="value-single-table"> 
													<xsl:with-param name="value" select="$lang.negative-side-effect.type-coreelement"/>
													<xsl:with-param name="fontSize" select="$font.size.key-value"/>
													<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
												</xsl:call-template>
									
								</fo:table-cell>


								<fo:table-cell column-number="3" 
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																margin-left="{$margin.left.table-cell}">

												<xsl:call-template name="value-single-table"> 
													<xsl:with-param name="value" select="$lang.negative-side-effect.type-intensity"/>
													<xsl:with-param name="fontSize" select="$font.size.key-value"/>
													<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
												</xsl:call-template>

								</fo:table-cell>

									<fo:table-cell column-number="4" 
																border-width="{$width.border}"
													            border-color="{$colour.border}"
																border-style="{$style.border}"
																border-end-style="hidden"
																margin-left="{$margin.left.table-cell}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.negative-side-effect.type-description"/>
								<xsl:with-param name="fontSize" select="$font.size.key-value"/>
								<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
							</xsl:call-template>

								</fo:table-cell>
							
							</fo:table-header>

							<fo:table-body>
								<xsl:for-each select="cerif:Type/cerif:Index">
								
								<fo:table-row>

									<fo:table-cell column-number="2" 		
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																border-start-style="hidden"
																margin-left="{$margin.left.table-cell}">
																
										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:CoreElement"/>
										</xsl:call-template>
									</fo:table-cell>
		
									<fo:table-cell column-number="3" 		
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																margin-left="{$margin.left.table-cell}">
																
										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:Intensity"/>
										</xsl:call-template>

									</fo:table-cell>

									<fo:table-cell column-number="4" 		
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																border-end-style="hidden"
																margin-left="{$margin.left.table-cell}">
																
										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:Description"/>
										</xsl:call-template>

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
					<xsl:with-param name="addRuler" select="'false'"/>
				</xsl:call-template>

			<xsl:call-template name="title">
				<xsl:with-param name="title" select="$lang.ethic.key-message"/>
				<xsl:with-param name="fontSize" select="$font.size.sub-sub-section-title"/>
			</xsl:call-template>

			<fo:block space-before="2pt" space-after="2pt">
				<!-- This block represents an empty line with 2 pt  -->
				</fo:block>

					<fo:table table-layout="fixed" 
												vertical-align="middle"
												border-before-style="hidden" 
												border-after-style="hidden"
												border-start-style="hidden"
												border-end-style="hidden">

							<fo:table-column column-width="proportional-column-width(1)"/>	<!-- this construct is used to align the table in a centred way	-->						
							<fo:table-column column-width="40.0%"/>
							<fo:table-column column-width="60.0%"/>	
							<fo:table-column column-width="proportional-column-width(1)"/> 	

							<fo:table-header>
								
								<fo:table-cell column-number="2"  					
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																border-start-style="hidden"
																margin-left="{$margin.left.table-cell}">

												<xsl:call-template name="value-single-table"> 
													<xsl:with-param name="value" select="$lang.ethic.reflection-aspecttype"/>
													<xsl:with-param name="fontSize" select="$font.size.key-value"/>
													<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
												</xsl:call-template>
									
								</fo:table-cell>

								<fo:table-cell column-number="3" 
																border-width="{$width.border}"
													            border-color="{$colour.border}"
																border-style="{$style.border}"
																border-end-style="hidden"
																margin-left="{$margin.left.table-cell}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.ethic.reflection-ethicdescription"/>
								<xsl:with-param name="fontSize" select="$font.size.key-value"/>
								<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
							</xsl:call-template>

								</fo:table-cell>
							
							</fo:table-header>

							<fo:table-body>
								<xsl:for-each select="cerif:Reflection/cerif:Index">
								
								<fo:table-row>

									<fo:table-cell column-number="2" 		
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																border-start-style="hidden"
																margin-left="{$margin.left.table-cell}">
																
										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:AspectType"/>
										</xsl:call-template>
									</fo:table-cell>

									<fo:table-cell column-number="3" 		
																border-width="{$width.border}"
																border-color="{$colour.border}"
																border-style="{$style.border}"
																border-end-style="hidden"
																margin-left="{$margin.left.table-cell}">
																
										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:EthicDescription"/>
										</xsl:call-template>

									</fo:table-cell>		

								</fo:table-row>
								</xsl:for-each>
							</fo:table-body>
							</fo:table>

				<xsl:call-template name="label-value-single-below">
				<xsl:with-param name="label" select="$lang.ethic.furtherdocuments"/>
				<xsl:with-param name="value" select="cerif:FurtherDocuments"/>
				<xsl:with-param name="addRuler" select="'false'"/>
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
						<xsl:with-param name="addRuler" select="'false'"/>
					</xsl:call-template>	

					<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.gender.reflection-researchquestion"/>
						<xsl:with-param name="value" select="cerif:Reflection/cerif:Researchquestion"/>
					</xsl:call-template>

					<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.gender.reflection-descriptionresearchquestion"/>
						<xsl:with-param name="value" select="cerif:Reflection/cerif:DescriptionResearchquestion"/>
					</xsl:call-template>

					<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.gender.reflection-stateofresearch"/>
						<xsl:with-param name="value" select="cerif:Reflection/cerif:StateOfResearch"/>
					</xsl:call-template>

					<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.gender.reflection-descriptionstateofresearch"/>
						<xsl:with-param name="value" select="cerif:Reflection/cerif:DescriptionStateOfResearch"/>
					</xsl:call-template>

					<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.gender.reflection-relevance"/>
						<xsl:with-param name="value" select="cerif:Reflection/cerif:Relevance"/>
					</xsl:call-template>

					<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.gender.reflection-descriptionrelevance"/>
						<xsl:with-param name="value" select="cerif:Reflection/cerif:DescriptionRelevance"/>
					</xsl:call-template>

					<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.gender.reflection-application"/>
						<xsl:with-param name="value" select="cerif:Reflection/cerif:Application"/>
					</xsl:call-template>

					<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.gender.reflection-descriptionapplication"/>
						<xsl:with-param name="value" select="cerif:Reflection/cerif:DescriptionApplication"/>
					</xsl:call-template>

					<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.gender.reflection-implementation"/>
						<xsl:with-param name="value" select="cerif:Reflection/cerif:Implementation"/>
					</xsl:call-template>

					<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.gender.reflection-descriptionimplementaiton"/>
						<xsl:with-param name="value" select="cerif:Reflection/cerif:DescriptionImplementaiton"/>
					</xsl:call-template>

					<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.gender.reflection-genderbalance"/>
						<xsl:with-param name="value" select="cerif:Reflection/cerif:GenderBalance"/>
					</xsl:call-template>

					<xsl:call-template name="label-value-single-below">
						<xsl:with-param name="label" select="$lang.gender.reflection-descriptiongenderbalance"/>
						<xsl:with-param name="value" select="cerif:Reflection/cerif:DescriptionGenderBalance"/>
					</xsl:call-template>

				</fo:block> 		

			</xsl:for-each>

		</xsl:if>

	</xsl:template>

<!--########################################################################-->
<!-- TEMPLATE FOR THE COOPERATION OVERVIEW -->	
<!--########################################################################-->

  <xsl:template name="cooperation-overview">

    <xsl:if test="count(cerif:ProjectPartner/cerif:Index) > 0 or count(cerif:CooperationPartner/cerif:Index) > 0 or count(cerif:TargetGroup/cerif:Index) > 0">

			<fo:block	margin-top="{$margin.top.main}"
								padding-before="{$padding.before.border}"
								padding-after="{$padding.after.border}"
								padding-start="{$padding.start.border}"
								padding-end="{$padding.end.border}">

				<fo:table table-layout="fixed" vertical-align="middle">
		
					<!-- define the table columns -->
					<fo:table-column column-width="proportional-column-width(1)"/>	<!-- this construct is used to align the table in a centred way -->							
					<fo:table-column column-width="24.25%"/>
					<fo:table-column column-width="24.25%"/>				
					<fo:table-column column-width="24.25%"/>
					<fo:table-column column-width="24.25%"/>
					<fo:table-column column-width="proportional-column-width(1)"/>  <!-- this is also part of central alignment approach --> 
																																					<!-- in addition the columns need to be numbered, omitting the first and last column --> 
					<!-- table header -->
					<fo:table-header>

						<!-- cooperation/partner -->
						<fo:table-cell column-number="2" 
													 border-width="{$width.border}"
													 border-color="{$colour.border}"
													 border-style="{$style.border}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.cooperation"/>
								<xsl:with-param name="fontSize" select="$font.size.key-value"/>
								<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
							</xsl:call-template>	

						</fo:table-cell>

						<!-- sector -->
						<fo:table-cell column-number="3" 
													 border-width="{$width.border}"
													 border-color="{$colour.border}"
													 border-style="{$style.border}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.cooperation.sector"/>
								<xsl:with-param name="fontSize" select="$font.size.key-value"/>
								<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
							</xsl:call-template>	

						</fo:table-cell>

						<!-- NACE/Destatis -->
						<fo:table-cell column-number="4" 
													 border-width="{$width.border}"
													 border-color="{$colour.border}"
													 border-style="{$style.border}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.cooperation.nace-destatis"/>
								<xsl:with-param name="fontSize" select="$font.size.key-value"/>
								<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
							</xsl:call-template>	

						</fo:table-cell>

						<!-- political level -->
						<fo:table-cell column-number="5" 
											 		 border-width="{$width.border}"
											 	   border-color="{$colour.border}"
											 		 border-style="{$style.border}">

							<xsl:call-template name="value-single-table"> 
								<xsl:with-param name="value" select="$lang.cooperation.political-level"/>
								<xsl:with-param name="fontSize" select="$font.size.key-value"/>
								<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
							</xsl:call-template>	

						</fo:table-cell>

					</fo:table-header>

					<!-- table body -->
					<fo:table-body>

						<!-- project partner -->
            <xsl:if test="count(cerif:ProjectPartner/cerif:Index) > 0">

              <!-- line with partner type -->
							<fo:table-row>

								<fo:table-cell column-number="2"
								               number-columns-spanned="4"
															 border-width="{$width.border}"
															 border-color="{$colour.border}"
															 border-style="{$style.border}">

									<xsl:call-template name="value-single-table"> 
										<xsl:with-param name="value" select="$lang.overview.project-partner"/>
										<xsl:with-param name="fontSize" select="$font.size.label-below"/>
										<xsl:with-param name="fontWeight" select="$font.weight.label"/>
									</xsl:call-template>	

								</fo:table-cell>

							</fo:table-row>
						
						  <!-- loop over project partners -->
						  <xsl:for-each select="cerif:ProjectPartner/cerif:Index">

							  <fo:table-row>

									<!-- partner name -->
									<fo:table-cell column-number="2" 
																 border-width="{$width.border}"
																 border-color="{$colour.border}"
																 border-style="{$style.border}">

										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:Acronym"/>
										</xsl:call-template>	

									</fo:table-cell>

									<!-- sector -->
									<fo:table-cell column-number="3" 
																 border-width="{$width.border}"
																 border-color="{$colour.border}"
																 border-style="{$style.border}">

										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:FieldOfActivity"/>
										</xsl:call-template>	

									</fo:table-cell>

									<!-- NACE/Destatis -->
									<fo:table-cell column-number="4" 
																 border-width="{$width.border}"
																 border-color="{$colour.border}"
																 border-style="{$style.border}">

										<xsl:call-template name="value-nace-destatis-table"> 
											<xsl:with-param name="nace" select="cerif:NACE"/>
											<xsl:with-param name="destatis" select="cerif:Destatis"/>
										</xsl:call-template>	

									</fo:table-cell>

									<!-- political level -->
									<fo:table-cell column-number="5" 
																 border-width="{$width.border}"
																 border-color="{$colour.border}"
																 border-style="{$style.border}">

										<xsl:call-template name="value-comma-list-table"> 
											<xsl:with-param name="value" select="cerif:PoliticalLevel"/>
										</xsl:call-template>	

									</fo:table-cell>

							  </fo:table-row>

							</xsl:for-each>

            </xsl:if>

						<!-- cooperation partner -->
            <xsl:if test="count(cerif:CooperationPartner/cerif:Index) > 0">

              <!-- line with partner type -->
							<fo:table-row>

								<fo:table-cell column-number="2"
								               number-columns-spanned="4"
															 border-width="{$width.border}"
															 border-color="{$colour.border}"
															 border-style="{$style.border}">

									<xsl:call-template name="value-single-table"> 
										<xsl:with-param name="value" select="$lang.overview.cooperation-partner"/>
										<xsl:with-param name="fontSize" select="$font.size.label-below"/>
										<xsl:with-param name="fontWeight" select="$font.weight.label"/>
									</xsl:call-template>	

								</fo:table-cell>

							</fo:table-row>
						
						  <!-- loop over cooperation partners -->
						  <xsl:for-each select="cerif:CooperationPartner/cerif:Index">

							  <fo:table-row>

									<!-- partner name -->
									<fo:table-cell column-number="2" 
																 border-width="{$width.border}"
																 border-color="{$colour.border}"
																 border-style="{$style.border}">

										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:OrganisationUnit/cerif:OrganisationName"/>
										</xsl:call-template>	

									</fo:table-cell>

									<!-- sector -->
									<fo:table-cell column-number="3" 
																 border-width="{$width.border}"
																 border-color="{$colour.border}"
																 border-style="{$style.border}">

										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:FieldOfActivity"/>
										</xsl:call-template>	

									</fo:table-cell>

									<!-- NACE/Destatis -->
									<fo:table-cell column-number="4" 
																 border-width="{$width.border}"
																 border-color="{$colour.border}"
																 border-style="{$style.border}">

										<xsl:call-template name="value-nace-destatis-table"> 
											<xsl:with-param name="nace" select="cerif:NACE"/>
											<xsl:with-param name="destatis" select="cerif:Destatis"/>
										</xsl:call-template>	

									</fo:table-cell>

									<!-- political level -->
									<fo:table-cell column-number="5" 
																 border-width="{$width.border}"
																 border-color="{$colour.border}"
																 border-style="{$style.border}">

										<xsl:call-template name="value-comma-list-table"> 
											<xsl:with-param name="value" select="cerif:PoliticalLevel"/>
										</xsl:call-template>	

									</fo:table-cell>

							  </fo:table-row>

							</xsl:for-each>

            </xsl:if>

						<!-- target group -->
            <xsl:if test="count(cerif:TargetGroup/cerif:Index) > 0">

              <!-- line with partner type -->
							<fo:table-row>

								<fo:table-cell column-number="2"
								               number-columns-spanned="4"
															 border-width="{$width.border}"
															 border-color="{$colour.border}"
															 border-style="{$style.border}">

									<xsl:call-template name="value-single-table"> 
										<xsl:with-param name="value" select="$lang.overview.target-group"/>
										<xsl:with-param name="fontSize" select="$font.size.label-below"/>
										<xsl:with-param name="fontWeight" select="$font.weight.label"/>
									</xsl:call-template>	

								</fo:table-cell>

							</fo:table-row>
						
						  <!-- loop over target groups -->
						  <xsl:for-each select="cerif:TargetGroup/cerif:Index">

							  <fo:table-row>

									<!-- partner name -->
									<fo:table-cell column-number="2" 
																 border-width="{$width.border}"
																 border-color="{$colour.border}"
																 border-style="{$style.border}">

										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:Title"/>
										</xsl:call-template>	

									</fo:table-cell>

									<!-- sector -->
									<fo:table-cell column-number="3" 
																 border-width="{$width.border}"
																 border-color="{$colour.border}"
																 border-style="{$style.border}">

										<xsl:call-template name="value-single-table"> 
											<xsl:with-param name="value" select="cerif:Classification/cerif:FieldOfActivity"/>
										</xsl:call-template>	

									</fo:table-cell>

									<!-- NACE/Destatis -->
									<fo:table-cell column-number="4" 
																 border-width="{$width.border}"
																 border-color="{$colour.border}"
																 border-style="{$style.border}">

										<xsl:call-template name="value-nace-destatis-table"> 
											<xsl:with-param name="nace" select="cerif:Classification/cerif:NACE"/>
											<xsl:with-param name="destatis" select="cerif:Classification/cerif:Destatis"/>
										</xsl:call-template>	

									</fo:table-cell>

									<!-- political level -->
									<fo:table-cell column-number="5" 
																 border-width="{$width.border}"
																 border-color="{$colour.border}"
																 border-style="{$style.border}">

										<xsl:call-template name="value-comma-list-table"> 
											<xsl:with-param name="value" select="cerif:Classification/cerif:PoliticalLevel"/>
										</xsl:call-template>	

									</fo:table-cell>

							  </fo:table-row>

							</xsl:for-each>

            </xsl:if>
					
					</fo:table-body>				

				</fo:table>		

      </fo:block>

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
									
					<xsl:call-template name="value-with-info">
						<xsl:with-param name="value" select="cerif:OrganisationUnit/cerif:OrganisationName"/>
						<xsl:with-param name="info" select="cerif:CooperationType"/>
						<xsl:with-param name="fontSize" select="$font.size.key-value"/>
						<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
						<xsl:with-param name="addRuler" select="'false'"/>
					</xsl:call-template>

					<xsl:call-template name="label-value-single-below"> 
						<xsl:with-param name="label" select="$lang.organisation.project-contribution"/>
						<xsl:with-param name="value" select="cerif:ProjectContribution"/>
				  </xsl:call-template>	

					<xsl:call-template name="label-value-single-below"> 
						<xsl:with-param name="label" select="$lang.organisation.form"/>
						<xsl:with-param name="value" select="cerif:OrganisationUnit/cerif:OrganisationType"/>
				  </xsl:call-template>	

					<xsl:call-template name="label-postcode-city-country-below">
						<xsl:with-param name="label" select="$lang.postcode-city-country"/>
						<xsl:with-param name="postCode" select="cerif:OrganisationUnit/cerif:PostCode"/>
						<xsl:with-param name="city" select="cerif:OrganisationUnit/cerif:City"/>
						<xsl:with-param name="country" select="cerif:OrganisationUnit/cerif:Country"/>
				  </xsl:call-template>

					<xsl:call-template name="label-value-comma-list-below"> 
						<xsl:with-param name="label" select="$lang.web-address"/>
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

					<xsl:call-template name="value-with-info">
						<xsl:with-param name="value" select="cerif:Title"/>
						<xsl:with-param name="info" select="cerif:Classification/cerif:BusinessSize"/>
						<xsl:with-param name="fontSize" select="$font.size.key-value"/>
						<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
						<xsl:with-param name="addRuler" select="'false'"/>
					</xsl:call-template>

					<xsl:call-template name="label-value-single-below"> 
						<xsl:with-param name="label" select="$lang.target-group.relevance"/>
						<xsl:with-param name="value" select="cerif:ProjectRelevance"/>
				  </xsl:call-template>	

					<xsl:call-template name="label-value-single-below"> 
						<xsl:with-param name="label" select="$lang.organisation.location-type"/>
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

	<xsl:template name="vertical-gap">
		<xsl:param name="margin" select="$margin.top.gap"/>

		<fo:block margin-left="{$margin.left.border}"
							margin-top="{$margin}">
		</fo:block>

	</xsl:template>

<!--==============================================================-->
<!-- ruler -->
<!--==============================================================-->

	<xsl:template name="ruler">

		<fo:block text-align="center">
			<fo:leader leader-pattern="rule" 
								 leader-length="{$width.ruler}" 
								 rule-style="{$style.ruler}" 
								 rule-thickness="{$width.border}" 
								 color="{$colour.border}"/>
		</fo:block>

	</xsl:template>

<!--==============================================================-->
<!-- empty table cell (just a hack using an empty ruler) -->
<!--==============================================================-->

	<xsl:template name="empty-table-cell-catcher">

		<fo:block>
			<fo:leader/>
		</fo:block>  

	</xsl:template>

<!--==============================================================-->	
<!-- title -->	
<!--==============================================================-->

	<xsl:template name="title">
		<xsl:param name="title"/>
		<xsl:param name="fontSize" select="$font.size.title"/>

		<fo:block margin-left="{$margin.left.border}"
							margin-top="{$margin.top.title}"
							font-size="{$fontSize}">

			<fo:inline font-weight="{$font.weight.title}"
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

	  <xsl:if test="$value">

			<xsl:if test="$addRuler = 'true'">
				<xsl:call-template name="ruler"/>
			</xsl:if>

		  <fo:block margin-left="{$margin.left.border}"
								font-size="{$fontSize}" 
			        	margin-top="{$margin.top.main}">

				<fo:inline font-weight="{$fontWeight}" 
										text-align="{$text.alignment.value}">
					<xsl:value-of select="$value"/> 
				</fo:inline>

			</fo:block>

	  </xsl:if>

	</xsl:template>

<!--==============================================================-->	
<!-- single value in a table cell -->
<!--==============================================================-->

	<xsl:template name="value-single-table">
	  <xsl:param name="value" select="''"/>
		<xsl:param name="fontSize" select="$font.size.standard"/>
		<xsl:param name="fontWeight" select="$font.weight.value"/>
    <xsl:param name="alignment" select="'left'"/>

		<fo:block text-align="{$alignment}" 
							font-size="{$fontSize}"
							font-weight="{$fontWeight}"
							margin-top="{$margin.top.main}"
							padding-before="{$padding.before.border}"
							padding-after="{$padding.after.border}"
							padding-start="{$padding.start.border}"
							padding-end="{$padding.end.border}"
							margin-left="{$margin.left.border}"
							margin-right="{$margin.right.border}">

			<xsl:value-of select="$value"/>
			
		</fo:block>

	</xsl:template>

<!--==============================================================-->	
<!-- single value with info (in parentheses)  -->
<!--==============================================================-->

	<xsl:template name="value-with-info">
	  <xsl:param name="value"/>
		<xsl:param name="info"/>
		<xsl:param name="fontSize" select="$font.size.standard"/>
		<xsl:param name="fontWeight" select="$font.weight.value"/>
    <xsl:param name="addRuler" select="'true'"/>

	  <xsl:if test="$value">

			<xsl:if test="$addRuler = 'true'">
				<xsl:call-template name="ruler"/>
			</xsl:if>

		  <fo:block margin-left="{$margin.left.border}"
								font-size="{$fontSize}" 
			        	margin-top="{$margin.top.main}">

				<fo:inline font-weight="{$fontWeight}" 
										text-align="{$text.alignment.value}">
					<xsl:value-of select="$value"/> 
				</fo:inline>

				<xsl:if test="$info">
					<fo:inline font-weight="{$fontWeight}" 
											text-align="{$text.alignment.value}">

						<xsl:text> (</xsl:text>											
						<xsl:value-of select="$info"/>
						<xsl:text>)</xsl:text> 

					</fo:inline>
			  </xsl:if>

			</fo:block>

	  </xsl:if>
	</xsl:template>

<!--==============================================================-->	
<!-- value list separated by commas -->
<!--==============================================================-->

	<xsl:template name="value-comma-list">
	  <xsl:param name="value"/>
		<xsl:param name="fontSize" select="$font.size.standard"/>
		<xsl:param name="fontWeight" select="$font.weight.value"/>

	  <xsl:if test="$value">

		  <fo:block margin-left="{$margin.left.border}"
								font-size="{$fontSize}" 
			        	margin-top="{$margin.top.main}">

				<fo:inline font-weight="{$fontWeight}" 
									 text-align="{$text.alignment.value}">

					<xsl:for-each select="$value">
						<xsl:value-of select="current()"/>
						<xsl:if test="position() != last()">, </xsl:if> 
					</xsl:for-each>

				</fo:inline>

			</fo:block>

	  </xsl:if>
	</xsl:template>

<!--==============================================================-->	
<!-- value list separated by commas in a table cell -->
<!--==============================================================-->

	<xsl:template name="value-comma-list-table">
	  <xsl:param name="value" select="''"/>
		<xsl:param name="fontSize" select="$font.size.standard"/>
		<xsl:param name="fontWeight" select="$font.weight.value"/>
		<xsl:param name="alignment" select="'left'"/>

		<fo:block text-align="{$alignment}" 
							font-size="{$fontSize}"
							font-weight="{$fontWeight}"
							margin-top="{$margin.top.main}"
							padding-before="{$padding.before.border}"
							padding-after="{$padding.after.border}"
							padding-start="{$padding.start.border}"
							padding-end="{$padding.end.border}"
							margin-left="{$margin.left.border}"
							margin-right="{$margin.right.border}">

			<xsl:for-each select="$value">
				<xsl:value-of select="current()"/>
				<xsl:if test="position() != last()">, </xsl:if> 
			</xsl:for-each>

		</fo:block>

	</xsl:template>

<!--==============================================================-->	
<!-- single set with icon and count in a table cell -->
<!--==============================================================-->

	<xsl:template name="value-icon-count-table">
	  <xsl:param name="iconFileName"/>
	  <xsl:param name="count" select="'0'"/>
		<xsl:param name="fontSize" select="$font.size.key-value"/>
		<xsl:param name="fontWeight" select="$font.weight.key-value"/>
    <xsl:param name="alignment" select="'center'"/>

		<fo:block text-align="{$alignment}" 
							font-size="{$fontSize}"
							font-weight="{$fontWeight}"
							margin-top="{$margin.top.main}"
							padding-before="{$padding.before.border}"
							padding-after="{$padding.after.border}"
							padding-start="{$padding.start.border}"
							padding-end="{$padding.end.border}"
							margin-left="{$margin.left.border}"
							margin-right="{$margin.right.border}">	

			<xsl:variable name="imageFile" select="concat('file:',$imageDirectory,$iconFileName)"/>

			<fo:external-graphic content-width="{$width.icon.overview}">  <!-- content-height="scale-to-fit" scaling="non-uniform" -->
				<xsl:attribute name="src">
					<xsl:value-of select="$imageFile"/>
				</xsl:attribute>
			</fo:external-graphic>

			<fo:inline vertical-align="top">
				<xsl:text>    </xsl:text> 
				<xsl:value-of select="$count"/>
			</fo:inline>

		</fo:block>

	</xsl:template>

<!--==============================================================-->	
<!-- value set with NACE and Destatis in a table cell -->
<!--==============================================================-->

	<xsl:template name="value-nace-destatis-table">
		<xsl:param name="nace" select="''"/>
		<xsl:param name="destatis" select="''"/>
		<xsl:param name="fontSize" select="$font.size.standard"/>
		<xsl:param name="fontWeight" select="$font.weight.value"/>
		<xsl:param name="alignment" select="'left'"/>

		<fo:block text-align="{$alignment}" 
							font-size="{$fontSize}"
							font-weight="{$fontWeight}"
							margin-top="{$margin.top.main}"
							padding-before="{$padding.before.border}"
							padding-after="{$padding.after.border}"
							padding-start="{$padding.start.border}"
							padding-end="{$padding.end.border}"
							margin-left="{$margin.left.border}"
							margin-right="{$margin.right.border}">

			<xsl:choose>
				<xsl:when test="$nace and $destatis">
					<xsl:value-of select="$nace"/>
					<xsl:text> / </xsl:text>
					<xsl:value-of select="$destatis"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$nace"/>
					<xsl:value-of select="$destatis"/>
				</xsl:otherwise>
			</xsl:choose>

		</fo:block>

	</xsl:template>

<!--==============================================================-->	
<!-- value set with issue date and author list in a table cell -->
<!--==============================================================-->

	<xsl:template name="value-issue-date-author-table">
		<xsl:param name="issueDate"/>
		<xsl:param name="author"/>
		<xsl:param name="fontSize" select="$font.size.standard"/>
		<xsl:param name="fontWeight" select="$font.weight.value"/>
		<xsl:param name="alignment" select="'left'"/>

    <xsl:if test="$issueDate or $author">

			<fo:block text-align="{$alignment}" 
								font-size="{$fontSize}"
								font-weight="{$fontWeight}"
								margin-top="{$margin.top.main}"
								padding-before="{$padding.before.border}"
								padding-after="{$padding.after.border}"
								padding-start="{$padding.start.border}"
								padding-end="{$padding.end.border}"
								margin-left="{$margin.left.border}"
								margin-right="{$margin.right.border}">

				<xsl:if test="$issueDate">
				  <xsl:text>(</xsl:text>
					<xsl:value-of select="$issueDate"/>
					<xsl:text>) </xsl:text>
    		</xsl:if>

				<xsl:if test="$author">
					<xsl:for-each select="$author">
						<xsl:value-of select="current()"/>
						<xsl:if test="position() != last()">, </xsl:if> 
					</xsl:for-each>
    		</xsl:if>

			</fo:block>

    </xsl:if>

	</xsl:template>

<!--==============================================================-->	
<!-- value set with issue date and author list table cell -->
<!--==============================================================-->

	<xsl:template name="value-publication-information-table">
		<xsl:param name="isPartOf"/>
		<xsl:param name="journal"/>
	  <xsl:param name="volume"/>
	  <xsl:param name="issue"/>
	  <xsl:param name="startPage"/>
	  <xsl:param name="endPage"/>
	  <xsl:param name="identifier"/>						
		<xsl:param name="fontSize" select="$font.size.standard"/>
		<xsl:param name="fontWeight" select="$font.weight.value"/>
		<xsl:param name="alignment" select="'left'"/>

    <xsl:if test="$isPartOf or $journal or $volume or $issue or $startPage or $endPage or $identifier">

			<fo:block text-align="{$alignment}" 
								font-size="{$fontSize}"
								font-weight="{$fontWeight}"
								margin-top="{$margin.top.main}"
								padding-before="{$padding.before.border}"
								padding-after="{$padding.after.border}"
								padding-start="{$padding.start.border}"
								padding-end="{$padding.end.border}"
								margin-left="{$margin.left.border}"
								margin-right="{$margin.right.border}">

				<xsl:if test="$isPartOf">
					<xsl:value-of select="$isPartOf"/>
					<xsl:text> </xsl:text>
    		</xsl:if>

				<xsl:if test="$journal">
				  <xsl:value-of select="$journal"/>
					<xsl:if test="$volume or $issue or $startPage or $endPage">
					  <xsl:text>, </xsl:text>
				  </xsl:if>
    		</xsl:if>

				<xsl:if test="$volume">
				  <xsl:value-of select="$volume"/>
					<xsl:if test="$issue or $startPage or $endPage">
					  <xsl:text>, </xsl:text>
				  </xsl:if>
    		</xsl:if>

				<xsl:if test="$issue">
				  <xsl:value-of select="$issue"/>
					<xsl:if test="$startPage or $endPage">
					  <xsl:text>, </xsl:text>
				  </xsl:if>
    		</xsl:if>

				<xsl:choose>
					<xsl:when test="$startPage and $endPage">
						<xsl:value-of select="$startPage"/>
						<xsl:text> - </xsl:text>
						<xsl:value-of select="$endPage"/>
						<xsl:text> </xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$startPage"/>
						<xsl:value-of select="$endPage"/>
						<xsl:text> </xsl:text>
					</xsl:otherwise>
				</xsl:choose>

				<xsl:if test="$identifier">
				  <xsl:value-of select="$identifier"/>
    		</xsl:if>

			</fo:block>

    </xsl:if>

	</xsl:template>

<!--==============================================================-->	
<!-- value set with postcode, city and and country -->
<!--==============================================================-->

	<xsl:template name="value-postcode-city-country">
	  <xsl:param name="postCode"/>
	  <xsl:param name="city"/>
		<xsl:param name="country"/>

	  <xsl:if test="$city"> <!--  use only city for check, this seems the most probable input -->
			<fo:block margin-left="{$margin.left.border}"
								font-size="{$font.size.standard}" 
								margin-top="{$margin.top.main}">

				<fo:inline font-weight="{$font.weight.value}" 
									 text-align="{$text.alignment.value}">

				  <xsl:if test="$postCode"> 
						<xsl:value-of select="$postCode"/>
						<xsl:text> </xsl:text>
					</xsl:if>
						
				  <xsl:value-of select="$city"/> 

					<xsl:if test="$country">
					  <xsl:text> / </xsl:text>
						<xsl:value-of select="$country"/> 
					</xsl:if>

				</fo:inline>

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
			<fo:block margin-left="{$margin.left.border}"
								font-size="{$font.size.standard}" 
								margin-top="{$margin.top.main}">

				<fo:inline font-weight="{$font.weight.value}" 
										text-align="{$text.alignment.value}">
					<xsl:value-of select="$name"/> 
				</fo:inline>

				<xsl:if test="$degree">
					<fo:inline font-weight="{$font.weight.value}" 
											text-align="{$text.alignment.value}">
						<xsl:text>, </xsl:text>					
						<xsl:value-of select="$degree"/> 
					</fo:inline>
			  </xsl:if>

				<xsl:if test="$gender">
					<fo:inline font-weight="{$font.weight.value}" 
											text-align="{$text.alignment.value}">
						<xsl:text> (</xsl:text>					
						<xsl:value-of select="$gender"/> 
						<xsl:text>)</xsl:text>
					</fo:inline>
			  </xsl:if>

			</fo:block>

	  </xsl:if>
	</xsl:template>

<!--==============================================================-->	
<!-- key -->
<!--==============================================================-->

	<xsl:template name="label-block">
	  <xsl:param name="label"/>
		<xsl:param name="fontSize" select="$font.size.standard"/>
		<xsl:param name="separator" select="''"/>

	  <xsl:if test="$label">

		  <fo:block margin-left="{$margin.left.border}"
			          margin-top="{$margin.top.main}"
			          font-size="{$fontSize}" 
								color="{$colour.label}">

				<fo:inline font-weight="{$font.weight.label}" 
									 text-align="{$text.alignment.label}">
					<xsl:value-of select="$label"/>
					<xsl:value-of select="$separator"/> 
				</fo:inline>

			</fo:block>

	  </xsl:if>
	</xsl:template>

<!--==============================================================-->	
<!-- label: single value -->
<!--==============================================================-->

	<xsl:template name="label-value-single">
	  <xsl:param name="label"/>
	  <xsl:param name="value"/>
		<xsl:param name="addRuler" select="'true'"/>

	  <xsl:if test="$value">

			<xsl:if test="$addRuler = 'true'">
				<xsl:call-template name="ruler"/>
			</xsl:if>

		  <fo:block margin-left="{$margin.left.border}"
								font-size="{$font.size.standard}" 
			        	margin-top="{$margin.top.main}">

				<fo:inline font-weight="{$font.weight.label}" 
				           text-align="{$text.alignment.label}"
									 color="{$colour.label}">
					<xsl:value-of select="$label"/>
					<xsl:text>: </xsl:text>
				</fo:inline>

				<fo:inline font-weight="{$font.weight.value}" 
									 text-align="{$text.alignment.value}">
					<xsl:value-of select="$value"/> 
				</fo:inline>

			</fo:block>

	  </xsl:if>
	</xsl:template>

<!--==============================================================-->	
<!-- label: single value (below label) -->
<!--==============================================================-->

	<xsl:template name="label-value-single-below">
		<xsl:param name="label"/>
		<xsl:param name="value"/>
		<xsl:param name="checkValue" select="'true'"/>
		<xsl:param name="addRuler" select="'true'"/>

		<xsl:choose>
			<xsl:when test="$checkValue = 'true'">

				<xsl:if test="$value">

					<xsl:if test="$addRuler = 'true'">
						<xsl:call-template name="ruler"/>
					</xsl:if>

					<!-- label -->
					<fo:block margin-left="{$margin.left.border}"
										font-size="{$font.size.label-below}" 
										margin-top="{$margin.top.main}">

						<fo:inline font-weight="{$font.weight.label}" 
											text-align="{$text.alignment.label}"
											color="{$colour.label}">
								<xsl:value-of select="$label"/>
						</fo:inline>

					</fo:block>

					<!-- value below -->
					<fo:block margin-left="{$margin.left.border}"
										font-size="{$font.size.standard}" 
										margin-top="{$margin.top.label-below}">

						<fo:inline font-weight="{$font.weight.value}" 
											text-align="{$text.alignment.value}">
							<xsl:value-of select="$value"/> 
						</fo:inline>

					</fo:block>

				</xsl:if>

			</xsl:when>
			<xsl:otherwise>

			<!-- do not check if a value exists, simply print everything -->
				<xsl:if test="$addRuler = 'true'">
					<xsl:call-template name="ruler"/>
				</xsl:if>

				<fo:block margin-left="{$margin.left.border}"
									font-size="{$font.size.label-below}" 
									margin-top="{$margin.top.main}">

					<fo:inline font-weight="{$font.weight.label}" 
										text-align="{$text.alignment.label}"
										color="{$colour.label}">
						<xsl:value-of select="$label"/>
					</fo:inline>
				</fo:block>

				<fo:block margin-left="{$margin.left.border}"
									font-size="{$font.size.standard}" 
									margin-top="{$margin.top.label-below}">
					<fo:inline font-weight="{$font.weight.value}" 
										text-align="{$text.alignment.value}">
						<xsl:value-of select="$value"/> 
					</fo:inline>
				</fo:block>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

<!--==============================================================-->	
<!-- label: single value (below label) in a table cell -->
<!--==============================================================-->

	<xsl:template name="label-value-single-below-table">
		<xsl:param name="label"/>
		<xsl:param name="value"/>
    <xsl:param name="alignment" select="'left'"/>
		<xsl:param name="addRuler" select="'false'"/>

    <xsl:if test="$value">

			<xsl:if test="$addRuler = 'true'">
				<xsl:call-template name="ruler"/>
			</xsl:if>

			<fo:block text-align="{$alignment}" 
								font-size="{$font.size.label-below}"
								font-weight="{$font.weight.label}"
								color="{$colour.label}"
								margin-top="{$margin.top.label-table}"
								padding-before="{$padding.before.border}"
								padding-after="{$padding.after.border}"
								padding-start="{$padding.start.border}"
								padding-end="{$padding.end.border}"
								margin-left="{$margin.left.border}"
								margin-right="{$margin.right.border}">

				<xsl:value-of select="$label"/>

			</fo:block> 

			<fo:block text-align="{$alignment}" 
								font-size="{$font.size.standard}"
								font-weight="{$font.weight.value}" 
								margin-top="{$margin.top.label-below-table}"
								padding-before="{$padding.before.border}"
								padding-after="{$padding.after.border}"
								padding-start="{$padding.start.border}"
								padding-end="{$padding.end.border}"
								margin-left="{$margin.left.border}"
								margin-right="{$margin.right.border}">

				<xsl:value-of select="$value"/>

			</fo:block>

    </xsl:if> 
    
	</xsl:template>

<!--==============================================================-->	
<!-- label: period (duration is assumed to be in months) -->
<!--==============================================================-->

	<xsl:template name="label-period">
	  <xsl:param name="label"/>
	  <xsl:param name="startDate"/>
		<xsl:param name="endDate"/>
		<xsl:param name="duration"/>
		<xsl:param name="addRuler" select="'true'"/>

	  <xsl:if test="$startDate and $endDate">

			<xsl:if test="$addRuler = 'true'">
				<xsl:call-template name="ruler"/>
			</xsl:if>

			<fo:block margin-left="{$margin.left.border}"
								font-size="{$font.size.standard}" 
								margin-top="{$margin.top.main}">

				<fo:inline font-weight="{$font.weight.label}" 
										text-align="{$text.alignment.label}"
										color="{$colour.label}">
					<xsl:value-of select="$label"/> 
					<xsl:text>: </xsl:text>
				</fo:inline>

				<fo:inline font-weight="{$font.weight.value}" 
									 text-align="{$text.alignment.value}">
					<xsl:value-of select="$startDate"/> 
				</fo:inline>

				<fo:inline font-weight="{$font.weight.value}" 
									 text-align="{$text.alignment.value}">
					<xsl:text> - </xsl:text>									 
					<xsl:value-of select="$endDate"/> 
				</fo:inline>

	      <xsl:if test="$duration">
					<fo:inline font-weight="{$font.weight.value}" 
										text-align="{$text.alignment.value}">

						<xsl:text> (</xsl:text>										
						<xsl:value-of select="$duration"/>
            <xsl:text> </xsl:text>
						
						  <xsl:choose>
						    <xsl:when test="$duration = '1'">
							    <xsl:value-of select="$lang.month.singular"/>
	 							</xsl:when>
  						  <xsl:otherwise>
    					    <xsl:value-of select="$lang.month.plural"/>
  							</xsl:otherwise>
							</xsl:choose>

						<xsl:text>)</xsl:text>										 
					</fo:inline>

        </xsl:if>

			</fo:block>

	  </xsl:if>
	</xsl:template>

<!--==============================================================-->	
<!-- label: period (below label) in a table cell (duration is assumed to be in hours) -->
<!--==============================================================-->

	<xsl:template name="label-period-below-table">
		<xsl:param name="label"/>
	  <xsl:param name="startDate"/>
		<xsl:param name="endDate"/>
		<xsl:param name="duration"/>
    <xsl:param name="alignment" select="'left'"/>
		<xsl:param name="addRuler" select="'false'"/>

    <xsl:if test="$startDate and $endDate">

			<xsl:if test="$addRuler = 'true'">
				<xsl:call-template name="ruler"/>
			</xsl:if>

			<fo:block text-align="{$alignment}" 
								font-size="{$font.size.label-below}"
								font-weight="{$font.weight.label}"
								color="{$colour.label}"
								margin-top="{$margin.top.label-table}"
								padding-before="{$padding.before.border}"
								padding-after="{$padding.after.border}"
								padding-start="{$padding.start.border}"
								padding-end="{$padding.end.border}"
								margin-left="{$margin.left.border}"
								margin-right="{$margin.right.border}">

				<xsl:value-of select="$label"/>

			</fo:block> 

			<fo:block text-align="{$alignment}" 
								font-size="{$font.size.standard}"
								font-weight="{$font.weight.value}" 
								margin-top="{$margin.top.label-below-table}"
								padding-before="{$padding.before.border}"
								padding-after="{$padding.after.border}"
								padding-start="{$padding.start.border}"
								padding-end="{$padding.end.border}"
								margin-left="{$margin.left.border}"
								margin-right="{$margin.right.border}">

				<xsl:choose>
					<xsl:when test="$startDate = $endDate">
						<xsl:value-of select="$startDate"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$startDate"/>
						<xsl:text> - </xsl:text>	
						<xsl:value-of select="$endDate"/>
					</xsl:otherwise>
				</xsl:choose>

	      <xsl:if test="$duration">
					<fo:inline font-weight="{$font.weight.value}" 
										 text-align="{$text.alignment.value}">

						<xsl:text> (</xsl:text>										
						<xsl:value-of select="$duration"/>
            <xsl:text> </xsl:text>
						
						  <xsl:choose>
						    <xsl:when test="$duration = '1'">
							    <xsl:value-of select="$lang.hour.singular"/>
	 							</xsl:when>
  						  <xsl:otherwise>
    					    <xsl:value-of select="$lang.hour.plural"/>
  							</xsl:otherwise>
							</xsl:choose>

						<xsl:text>)</xsl:text>										 
					</fo:inline>

        </xsl:if>				

			</fo:block>

    </xsl:if> 
    
	</xsl:template>

<!--==============================================================-->	
<!-- label: amount and currency -->
<!--==============================================================-->

	<xsl:template name="label-amount-currency">
		<xsl:param name="label"/>
	  <xsl:param name="amount"/>
	  <xsl:param name="currency" select="'Euro'"/>
		<xsl:param name="addRuler" select="'true'"/>

	  <xsl:if test="$amount"> <!--  use only amount for check, this seems the most probable input -->

			<xsl:if test="$addRuler = 'true'">
				<xsl:call-template name="ruler"/>
			</xsl:if>

			<fo:block	margin-left="{$margin.left.border}"
						    font-size="{$font.size.standard}" 
						    margin-top="{$margin.top.main}">

				<fo:inline font-weight="{$font.weight.label}" 
					  			 text-align="{$text.alignment.label}"
									 color="{$colour.label}">
					<xsl:value-of select="$label"/> 

				</fo:inline>

			</fo:block>

			<fo:block	margin-left="{$margin.left.border}"
								font-size="{$font.size.standard}" 
								margin-top="{$margin.top.main}">

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

	  </xsl:if>
	</xsl:template>

<!--==============================================================-->	
<!-- label: value list separated by commas -->
<!--==============================================================-->

	<xsl:template name="label-value-comma-list">
		<xsl:param name="label"/>
	  <xsl:param name="value"/>
		<xsl:param name="addRuler" select="'true'"/>

	  <xsl:if test="$value">

			<xsl:if test="$addRuler = 'true'">
				<xsl:call-template name="ruler"/>
			</xsl:if>

		  <fo:block margin-left="{$margin.left.border}" 
								font-size="{$font.size.standard}" 
			          margin-top="{$margin.top.main}">

				<fo:inline font-weight="{$font.weight.label}" 
									 text-align="{$text.alignment.label}"
									 color="{$colour.label}">
					<xsl:value-of select="$label"/> 
					<xsl:text>: </xsl:text>
				</fo:inline>

				<fo:inline font-weight="{$font.weight.value}" 
									 text-align="{$text.alignment.value}">

					<xsl:for-each select="$value">
					  <xsl:value-of select="current()"/>
					  <xsl:if test="position() != last()">, </xsl:if> 
					</xsl:for-each>

				</fo:inline>

			</fo:block>

		</xsl:if>
	</xsl:template>	

<!--==============================================================-->	
<!-- label: value list separated by commas (below label) -->
<!--==============================================================-->

	<xsl:template name="label-value-comma-list-below">
		<xsl:param name="label"/>
	  <xsl:param name="value"/>
		<xsl:param name="addRuler" select="'true'"/>

	  <xsl:if test="$value">

			<xsl:if test="$addRuler = 'true'">
				<xsl:call-template name="ruler"/>
			</xsl:if>

        <!-- label -->
		  <fo:block margin-left="{$margin.left.border}" 
					      font-size="{$font.size.label-below}" 
			          margin-top="{$margin.top.main}">

				<fo:inline  font-weight="{$font.weight.label}" 
							      text-align="{$text.alignment.label}"
							      color="{$colour.label}">
					<xsl:value-of select="$label"/> 
				</fo:inline>

		  </fo:block>

        <!-- value below -->
		  <fo:block margin-left="{$margin.left.border}" 
					      font-size="{$font.size.standard}" 
			          margin-top="{$margin.top.label-below}">

				<fo:inline font-weight="{$font.weight.value}" 
									 text-align="{$text.alignment.value}">

					<xsl:for-each select="$value">
					  <xsl:value-of select="current()"/>
					  <xsl:if test="position() != last()">, </xsl:if> 
					</xsl:for-each>

				</fo:inline>

			</fo:block>

		</xsl:if>
	</xsl:template>	

<!--==============================================================-->	
<!-- label: value list separated by commas (below label) in a table cell -->
<!--==============================================================-->

	<xsl:template name="label-value-comma-list-below-table">
		<xsl:param name="label"/>
		<xsl:param name="value"/>
    <xsl:param name="alignment" select="'left'"/>
		<xsl:param name="addRuler" select="'false'"/>

		<xsl:if test="$value">

			<xsl:if test="$addRuler = 'true'">
				<xsl:call-template name="ruler"/>
			</xsl:if>

			<fo:block text-align="{$alignment}" 
								font-size="{$font.size.label-below}"
								font-weight="{$font.weight.label}"
								color="{$colour.label}"
								margin-top="{$margin.top.label-table}"
								padding-before="{$padding.before.border}"
								padding-after="{$padding.after.border}"
								padding-start="{$padding.start.border}"
								padding-end="{$padding.end.border}"
								margin-left="{$margin.left.border}"
								margin-right="{$margin.right.border}">

				<xsl:value-of select="$label"/>

			</fo:block> 

			<fo:block text-align="{$alignment}" 
								font-size="{$font.size.standard}"
								font-weight="{$font.weight.value}" 
								margin-top="{$margin.top.label-below-table}"
								padding-before="{$padding.before.border}"
								padding-after="{$padding.after.border}"
								padding-start="{$padding.start.border}"
								padding-end="{$padding.end.border}"
								margin-left="{$margin.left.border}"
								margin-right="{$margin.right.border}">

				<xsl:for-each select="$value">
					<xsl:value-of select="current()"/>
					<xsl:if test="position() != last()">, </xsl:if> 
				</xsl:for-each>

			</fo:block>

		</xsl:if>

	</xsl:template>

<!--==============================================================-->	
<!-- label: every value in a separated line (below label) -->
<!--==============================================================-->

	<xsl:template name="label-value-line-list-below">
		<xsl:param name="label"/>
	  <xsl:param name="value"/>
		<xsl:param name="addRuler" select="'true'"/>

		<xsl:if test="$value"> <!-- do only if there are values -->

			<xsl:if test="$addRuler = 'true'">
				<xsl:call-template name="ruler"/>
			</xsl:if>

			<fo:block margin-left="{$margin.left.border}"
								font-size="{$font.size.label-below}" 
			          margin-top="{$margin.top.main}">
						
        <!-- label -->
				<fo:inline font-weight="{$font.weight.label}" 
									 text-align="{$text.alignment.label}"
									 color="{$colour.label}">
					<xsl:value-of select="$label"/> 
				</fo:inline>

			</fo:block>

      <!-- value below -->
			<fo:list-block margin-left="{$margin.left.border}"
			               font-size="{$font.size.standard}"
										 margin-top="{$margin.top.label-below}"
										 font-weight="{$font.weight.value}" 
									   text-align="{$text.alignment.value}">

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
<!-- label: postcode city / country (below label) -->
<!--==============================================================-->

	<xsl:template name="label-postcode-city-country-below">
		<xsl:param name="label"/>
	  <xsl:param name="postCode"/>
	  <xsl:param name="city"/>
		<xsl:param name="country"/>
		<xsl:param name="addRuler" select="'true'"/>

	  <xsl:if test="$city"> <!--  use only city for check, this seems the most probable input -->

			<xsl:if test="$addRuler = 'true'">
				<xsl:call-template name="ruler"/>
			</xsl:if>

      <!-- label -->
			<fo:block margin-left="{$margin.left.border}"
								font-size="{$font.size.label-below}" 
								margin-top="{$margin.top.main}">

				<fo:inline font-weight="{$font.weight.label}" 
										text-align="{$text.alignment.label}"
										color="{$colour.label}">
					<xsl:value-of select="$label"/>
				</fo:inline>

			</fo:block>

        <!-- value below -->
			<fo:block margin-left="{$margin.left.border}"
								font-size="{$font.size.standard}" 
								margin-top="{$margin.top.label-below}">

				<fo:inline font-weight="{$font.weight.value}" 
									text-align="{$text.alignment.value}">

				  <xsl:if test="$postCode"> 
            <xsl:value-of select="$postCode"/>
						<xsl:text> </xsl:text>
					</xsl:if>	

				  <xsl:value-of select="$city"/> 

					<xsl:if test="$country">
					  <xsl:text> / </xsl:text>
						<xsl:value-of select="$country"/> 
					</xsl:if>

				</fo:inline>

			</fo:block>

	  </xsl:if>
	</xsl:template>

<!--==============================================================-->	
<!-- label: postcode city / country (below label) in a table cell -->
<!--==============================================================-->

	<xsl:template name="label-postcode-city-country-below-table">
		<xsl:param name="label"/>
	  <xsl:param name="postCode"/>
	  <xsl:param name="city"/>
		<xsl:param name="country"/>
    <xsl:param name="alignment" select="'left'"/>
		<xsl:param name="addRuler" select="'false'"/>

    <xsl:if test="$city"> <!--  use only city for check, this seems the most probable input -->

			<xsl:if test="$addRuler = 'true'">
				<xsl:call-template name="ruler"/>
			</xsl:if>

			<fo:block text-align="{$alignment}" 
								font-size="{$font.size.label-below}"
								font-weight="{$font.weight.label}"
								color="{$colour.label}"
								margin-top="{$margin.top.label-table}"
								padding-before="{$padding.before.border}"
								padding-after="{$padding.after.border}"
								padding-start="{$padding.start.border}"
								padding-end="{$padding.end.border}"
								margin-left="{$margin.left.border}"
								margin-right="{$margin.right.border}">

				<xsl:value-of select="$label"/>

			</fo:block> 

			<fo:block text-align="{$alignment}" 
								font-size="{$font.size.standard}"
								font-weight="{$font.weight.value}" 
								margin-top="{$margin.top.label-below-table}"
								padding-before="{$padding.before.border}"
								padding-after="{$padding.after.border}"
								padding-start="{$padding.start.border}"
								padding-end="{$padding.end.border}"
								margin-left="{$margin.left.border}"
								margin-right="{$margin.right.border}">
		
				<xsl:if test="$postCode"> 
          <xsl:value-of select="$postCode"/>
					<xsl:text> </xsl:text>
				</xsl:if>	

				<xsl:value-of select="$city"/>

				<xsl:if test="$country">
					<xsl:text> / </xsl:text>
					<xsl:value-of select="$country"/> 
				</xsl:if>

			</fo:block>

    </xsl:if> 
    
	</xsl:template>

<!--==============================================================-->
<!-- label: author (affiliation) separated by commas -->
<!--==============================================================-->

	<xsl:template name="label-author-affilation-comma-list">
		<xsl:param name="label"/>
	  <xsl:param name="value"/>
		<xsl:param name="addRuler" select="'true'"/>

	  <xsl:if test="$value">

			<xsl:if test="$addRuler = 'true'">
				<xsl:call-template name="ruler"/>
			</xsl:if>

		  <fo:block margin-left="{$margin.left.border}" 
								font-size="{$font.size.standard}" 
			          margin-top="{$margin.top.main}">

				<fo:inline font-weight="{$font.weight.label}" 
									 text-align="{$text.alignment.label}"
									 color="{$colour.label}">
					<xsl:value-of select="$label"/>
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

		</xsl:if>

  </xsl:template>	

<!--==============================================================-->
<!-- label: author (affiliation) separated by commas (below label) -->
<!--==============================================================-->

	<xsl:template name="label-author-affilation-comma-list-below">
		<xsl:param name="label"/>
		<xsl:param name="value"/>
		<xsl:param name="addRuler" select="'true'"/>

		<xsl:if test="$value">

			<xsl:if test="$addRuler = 'true'">
				<xsl:call-template name="ruler"/>
			</xsl:if>

      <!-- label -->
			<fo:block margin-left="{$margin.left.border}" 
					 	    font-size="{$font.size.label-below}" 
						    margin-top="{$margin.top.main}">

				<fo:inline font-weight="{$font.weight.label}" 
								   text-align="{$text.alignment.label}"
								   color="{$colour.label}">
					<xsl:value-of select="$label"/>
				</fo:inline>

			</fo:block>

      <!-- value below -->
			<fo:block margin-left="{$margin.left.border}"
						    font-size="{$font.size.standard}" 
						    margin-top="{$margin.top.label-below}">

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
						<xsl:with-param name="fontSize" select="$font.size.key-value"/>
						<xsl:with-param name="fontWeight" select="$font.weight.key-value"/>
						<xsl:with-param name="addRuler" select="'false'"/>
					</xsl:call-template>
 
        <!-- description -->
					<xsl:if test="cerif:Description">							

						<xsl:call-template name="value-single">
							<xsl:with-param name="value" select="cerif:Description"/>
						</xsl:call-template>

					</xsl:if>
          
        </fo:block>

		  </xsl:for-each> 
	
		</xsl:if>
	
	</xsl:template>

<!--==============================================================-->
<!-- embedded PDFs -->
<!--==============================================================-->

	<xsl:template name="embedded-pdf">
		<xsl:param name="imageDir" />

		<xsl:if test="cerif:EmbeddedPdfs">
			<xsl:for-each select="cerif:EmbeddedPdfs/cerif:EmbeddedPdf">
				<xsl:variable name="pdfPath" select="concat('file:',$imageDir,'/',current())" />

				<fox:external-document content-type="pdf"
																content-width="scale-to-fit" 
																width="24cm">
					<xsl:attribute name="src">
						<xsl:value-of select="$pdfPath" />
					</xsl:attribute>
				</fox:external-document>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>
    
  <!--==============================================================-->
	<!-- graphs -->
	<!--==============================================================-->
    
	<xsl:template name="graphs">
		<xsl:param name="imageDir"/>

		<xsl:if test="cerif:Graphs">
			<xsl:for-each select="cerif:Graphs/cerif:Graph">
			  <xsl:variable name="graphPath" select="concat('file:',$imageDir,'/',current())" />

				<fo:block font-weight="bold" text-align="center">

					<xsl:call-template name="image-print">
						<xsl:with-param name="imageDir" select="$imageDir"/>
						<xsl:with-param name="nodeValue" select="current()"/>
					</xsl:call-template>

				</fo:block>

			</xsl:for-each>
		</xsl:if>
	</xsl:template>
    
<!--==============================================================-->
<!-- screenshots of the impact pathway -->
<!--==============================================================-->
    
	<xsl:template name="impactpathway-screenshots">
		<xsl:param name="imageDir"/>

		<xsl:if test="cerif:ImpactPathway">
			<xsl:for-each select="cerif:ImpactPathway/cerif:Screenshots/cerif:Screenshot">

				<fo:block font-weight="bold" text-align="center">

					<xsl:call-template name="image-print">
					<xsl:with-param name="imageDir" select="$imageDir"/>
					<xsl:with-param name="nodeValue" select="current()"/>
					</xsl:call-template>

				</fo:block>

			</xsl:for-each>
		</xsl:if>
	</xsl:template>
    
<!--==============================================================-->
<!-- screenshots of the working plan -->
<!--==============================================================-->
    
	<xsl:template name="workingplan-screenshots">
		<xsl:param name="imageDir"/>

		<xsl:if test="cerif:WorkPackage">
			<xsl:for-each select="cerif:WorkPackage/cerif:Screenshots/cerif:Screenshot">

				<fo:block font-weight="bold" text-align="center">

					<xsl:call-template name="image-print">
						<xsl:with-param name="imageDir" select="$imageDir"/>
						<xsl:with-param name="nodeValue" select="current()"/>
					</xsl:call-template>

				</fo:block>

			</xsl:for-each>
		</xsl:if>
	</xsl:template>
    
<!--==============================================================-->
<!-- image print -->
<!--==============================================================-->

	<xsl:template name="image-print">
		<xsl:param name="imageDir"/>
		<xsl:param name="nodeValue"/>
	  <xsl:variable name="imagePath" select="concat('file:',$imageDir,'/',$nodeValue)" />

		<fo:external-graphic content-height="scale-to-fit" content-width="16cm" scaling="uniform">
			<xsl:attribute name="src">
				<xsl:value-of select="$imagePath" />
			</xsl:attribute>
		</fo:external-graphic>

	</xsl:template>

</xsl:stylesheet>