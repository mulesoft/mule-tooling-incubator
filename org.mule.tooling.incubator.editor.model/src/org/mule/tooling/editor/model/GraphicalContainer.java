package org.mule.tooling.editor.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "graphical-container")
public class GraphicalContainer extends AbstractContainer {

    private String pathExpression;
    private String childrenCreatorId;
    private Boolean contributesToPath;
    private String editPolicyFactoryId;
    private String layoutFactoryId;
    private String updateParticipantId;
    private String prompt;

    @Override
    public void accept(IElementVisitor visitor) {
        visitor.visit(this);
    }

    @XmlAttribute
    public String getChildrenCreatorId() {
        return childrenCreatorId;
    }

    public void setChildrenCreatorId(String childrenCreatorId) {
        this.childrenCreatorId = childrenCreatorId;
    }

    @XmlAttribute
    public Boolean getContributesToPath() {
        return contributesToPath;
    }

    public void setContributesToPath(Boolean contributesToPath) {
        this.contributesToPath = contributesToPath;
    }

    @XmlAttribute
    public String getEditPolicyFactoryId() {
        return editPolicyFactoryId;
    }

    public void setEditPolicyFactoryId(String editPolicyFactoryId) {
        this.editPolicyFactoryId = editPolicyFactoryId;
    }

    @XmlAttribute
    public String getLayoutFactoryId() {
        return layoutFactoryId;
    }

    public void setLayoutFactoryId(String layoutFactoryId) {
        this.layoutFactoryId = layoutFactoryId;
    }

    @XmlAttribute
    public String getUpdateParticipantId() {
        return updateParticipantId;
    }

    public void setUpdateParticipantId(String updateParticipantId) {
        this.updateParticipantId = updateParticipantId;
    }

    @XmlAttribute
    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    @XmlAttribute
    public String getPathExpression() {
        return pathExpression;
    }

    public void setPathExpression(String pathExpression) {
        this.pathExpression = pathExpression;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((childrenCreatorId == null) ? 0 : childrenCreatorId.hashCode());
        result = prime * result + ((contributesToPath == null) ? 0 : contributesToPath.hashCode());
        result = prime * result + ((editPolicyFactoryId == null) ? 0 : editPolicyFactoryId.hashCode());
        result = prime * result + ((layoutFactoryId == null) ? 0 : layoutFactoryId.hashCode());
        result = prime * result + ((pathExpression == null) ? 0 : pathExpression.hashCode());
        result = prime * result + ((prompt == null) ? 0 : prompt.hashCode());
        result = prime * result + ((updateParticipantId == null) ? 0 : updateParticipantId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        GraphicalContainer other = (GraphicalContainer) obj;
        if (childrenCreatorId == null) {
            if (other.childrenCreatorId != null)
                return false;
        } else if (!childrenCreatorId.equals(other.childrenCreatorId))
            return false;
        if (contributesToPath == null) {
            if (other.contributesToPath != null)
                return false;
        } else if (!contributesToPath.equals(other.contributesToPath))
            return false;
        if (editPolicyFactoryId == null) {
            if (other.editPolicyFactoryId != null)
                return false;
        } else if (!editPolicyFactoryId.equals(other.editPolicyFactoryId))
            return false;
        if (layoutFactoryId == null) {
            if (other.layoutFactoryId != null)
                return false;
        } else if (!layoutFactoryId.equals(other.layoutFactoryId))
            return false;
        if (pathExpression == null) {
            if (other.pathExpression != null)
                return false;
        } else if (!pathExpression.equals(other.pathExpression))
            return false;
        if (prompt == null) {
            if (other.prompt != null)
                return false;
        } else if (!prompt.equals(other.prompt))
            return false;
        if (updateParticipantId == null) {
            if (other.updateParticipantId != null)
                return false;
        } else if (!updateParticipantId.equals(other.updateParticipantId))
            return false;
        return true;
    }

}
