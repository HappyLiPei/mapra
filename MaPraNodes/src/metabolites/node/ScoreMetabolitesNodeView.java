package metabolites.node;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "ScoreMetabolites" Node.
 * 
 *
 * @author Marie-Sophie Friedl
 */
public class ScoreMetabolitesNodeView extends NodeView<ScoreMetabolitesNodeModel> {

    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link ScoreMetabolitesNodeModel})
     */
    protected ScoreMetabolitesNodeView(final ScoreMetabolitesNodeModel nodeModel) {
        super(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {
        ScoreMetabolitesNodeModel nodeModel = 
            (ScoreMetabolitesNodeModel)getNodeModel();
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

