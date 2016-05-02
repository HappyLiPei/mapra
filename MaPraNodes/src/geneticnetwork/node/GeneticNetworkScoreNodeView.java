package geneticnetwork.node;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "GeneticNetworkScore" Node.
 * 
 *
 * @author Marie-Sophie Friedl
 */
public class GeneticNetworkScoreNodeView extends NodeView<GeneticNetworkScoreNodeModel> {

    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link GeneticNetworkScoreNodeModel})
     */
    protected GeneticNetworkScoreNodeView(final GeneticNetworkScoreNodeModel nodeModel) {
        super(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {
        GeneticNetworkScoreNodeModel nodeModel = 
            (GeneticNetworkScoreNodeModel)getNodeModel();
        assert nodeModel != null;       
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClose() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onOpen() {
    }

}

