package phenomizer;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "Phenomizer" Node.
 * 
 *
 * @author 
 */
public class PhenomizerNodeView extends NodeView<PhenomizerNodeModel> {

    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link PhenomizerNodeModel})
     */
    protected PhenomizerNodeView(final PhenomizerNodeModel nodeModel) {
        super(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {
        PhenomizerNodeModel nodeModel = 
            (PhenomizerNodeModel)getNodeModel();
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

