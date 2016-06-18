package phenotogeno.node;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "PhenoToGenoNode" Node.
 * 
 *
 * @author Marie-Sophie Friedl
 */
public class PhenoToGenoNodeView extends NodeView<PhenoToGenoNodeModel> {

    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link PhenoToGenoNodeModel})
     */
    protected PhenoToGenoNodeView(final PhenoToGenoNodeModel nodeModel) {
        super(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {
        PhenoToGenoNodeModel nodeModel = 
            (PhenoToGenoNodeModel)getNodeModel();
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

