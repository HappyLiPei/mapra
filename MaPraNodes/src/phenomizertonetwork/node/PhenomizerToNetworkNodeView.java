package phenomizertonetwork.node;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "PhenomizerToNetwork" Node.
 * 
 *
 * @author 
 */
public class PhenomizerToNetworkNodeView extends NodeView<PhenomizerToNetworkNodeModel> {

    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link PhenomizerToNetworkNodeModel})
     */
    protected PhenomizerToNetworkNodeView(final PhenomizerToNetworkNodeModel nodeModel) {
        super(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {
        PhenomizerToNetworkNodeModel nodeModel = 
            (PhenomizerToNetworkNodeModel)getNodeModel();
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

