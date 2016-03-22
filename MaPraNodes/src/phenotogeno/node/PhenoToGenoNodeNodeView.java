package phenotogeno.node;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "PhenoToGenoNode" Node.
 * 
 *
 * @author Marie-Sophie Friedl
 */
public class PhenoToGenoNodeNodeView extends NodeView<PhenoToGenoNodeNodeModel> {

    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link PhenoToGenoNodeNodeModel})
     */
    protected PhenoToGenoNodeNodeView(final PhenoToGenoNodeNodeModel nodeModel) {
        super(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {
        PhenoToGenoNodeNodeModel nodeModel = 
            (PhenoToGenoNodeNodeModel)getNodeModel();
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

