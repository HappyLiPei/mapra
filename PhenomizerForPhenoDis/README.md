# Phenomizer for PhenoDis
This repository was created during bioinformatics master practical (summer 2015) at Helmholtz Zentrum.
It contains an implementation of the Phenomizer algorithm for the PhenoDis database.
The Phenomizer for PhenoDis is realized as a KNIME node.
As the PhenoDis database is not yet publicly available, you can test the tool with the dummy data provided in this repository.

Content:

* MaPraNodes: source code of Phenomizer for PhenoDis
* TestData: dummy data representing diseases, symptoms, disease-symptom association, queries, all-against-all matrices and score distributions
* phenomizer_1.4.2.jar: compiled Phenomizer KNIME nodes, just copy this file into the dropins folder of your KNIME installation to use Phenomizer for PhenoDis
* DecisionSupportForRareDiseases.pdf: paper presenting Phenomizer for PhenoDis