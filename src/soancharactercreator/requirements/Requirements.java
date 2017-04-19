package soancharactercreator.requirements;

import soancharactercreator.BaseSkill;
import soancharactercreator.Header;
import soancharactercreator.PlayerCharacter;
import soancharactercreator.Skill;

/**
 *
 * 
 */
public class Requirements {
    
    private final HeaderRequirement headerRequirement_;
    private final SkillRequirement skillRequirement_;
    private final TierRequirement tierRequirement_;
    
    public Requirements(HeaderRequirement header, SkillRequirement skill, TierRequirement tier) {
        headerRequirement_ = header;
        skillRequirement_ = skill;
        tierRequirement_ = tier;
    }
    
    public boolean hasHeaderRequirement() {
        return headerRequirement_ != null;
    }
    
    public Header getRequiredHeader() {
        return headerRequirement_.getHeader();
    }
    
    public boolean hasSkillRequirement() {
        return skillRequirement_ != null;
    }
    
    public boolean hasTierRequirement() {
        return tierRequirement_ != null;
    }
    
    public boolean meetsRequirements(PlayerCharacter character, Skill skill) {
        
        if(hasHeaderRequirement()) {
            if(!headerRequirement_.meetsRequirements(character, skill)) {
                return false;
            }
        }
        
        if(hasSkillRequirement()) {
            if(!skillRequirement_.meetsRequirements(character, skill)) {
                return false;
            }
        }
        
        if(hasTierRequirement()) {
            if(!tierRequirement_.meetsRequirements(character, skill)) {
                return false;
            }
        }
        
        return true;
    }
    
    public SkillRequirement getSkillRequirement() {
        return skillRequirement_;
    }
    
    public TierRequirement getTierRequirement() {
        return tierRequirement_;
    }
    
}
