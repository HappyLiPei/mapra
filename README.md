# PheNoBo

**PheNoBo** is a set of KNIME nodes for predicting causal genes of rare diseases by combining **Phe**notype, ge**No**type and meta**Bo**type data.

This repository was created during the bioinformatics master practical (summer 2015) at Helmholtz Zentrum.
Originally, the repository contained Phenomizer for PhenoDis.
During summer 2016 Phenomizer for PhenoDis was further extended and included in PheNoBo.

Installation of PheNoBo:

1. Install KNIME (recommended version 3.1)
2. Copy *phenobo_2.1.6.jar* into the folder *dropins* of your KNIME installation
3. Import *PheNoBo_KNIME_Workflow.zip* into KNIME (via File -> Import KNIME Workflow)

As the real data for running PheNoBo is not yet publicly available, you can test all KNIME nodes with the dummy data provided in this repository.

Content:

* MaPraNodes: source code of PheNoBo (including Phenomizer for PhenoDis)
* PhenomizerForPhenoDis: original implmentation and documentation of Phenomizer for PhenoDis
* TestData: dummy data representing diseases, metabolites, genes and their associations among each other
* PheNoBo_KNIME_Workflow.zip: the KNIME workflow of the PheNoBo nodes
* phenobo_2.1.6.jar: executable of PheNoBo for KNIME