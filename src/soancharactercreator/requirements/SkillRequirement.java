
package soancharactercreator.requirements;

import java.util.Set;
import org.json.simple.JSONArray;
import soancharactercreator.BaseSkill;
import soancharactercreator.PlayerCharacter;
import soancharactercreator.Skill;

/**
 *
 * 
 */
public class SkillRequirement extends Requirement {
    
    private final Set<Set<BaseSkill>> requiredSkillsSet_;
    
    public SkillRequirement(Set<Set<BaseSkill>> requiredSkills) {
        requiredSkillsSet_ = requiredSkills;
    }

    @Override
    public boolean meetsRequirements(PlayerCharacter character, Skill skill) {
        
        for(Set<BaseSkill> skillSet : requiredSkillsSet_) {
            
            boolean hasSkill = false;
            
            for(BaseSkill baseSkill : skillSet) {
                if(character.hasSkill(baseSkill)) {
                    hasSkill = true;
                    break;
                }       
            }
            
            if(!hasSkill) {
                return false;
            }
        }
        
        return true;
    }
    
    public boolean isStrictlyRequiredSkill(BaseSkill skill) {
        
        for(Set<BaseSkill> skillSet : requiredSkillsSet_) {
            
            if(skillSet.size() == 1 && skillSet.contains(skill)) {
                return true;
            }
        }
        
        return false;
    }
    
    
}
