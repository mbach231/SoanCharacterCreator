/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soancharactercreator;

import soancharactercreator.SkillLoader.EntryType;
import soancharactercreator.requirements.Requirements;

/**
 *
 *
 */
public class Skill extends BaseSkill {

    private final Requirements requirements_;
    private final String activationCost_;
    private final Integer tierRank_;
    private final String tierType_;

    public Skill(String name, int cost, String actCost, Integer rank, String type, Requirements requirements) {
        super(name, cost, EntryType.SKILL);
        activationCost_ = actCost;
        tierRank_ = rank;
        tierType_ = type;
        requirements_ = requirements;
    }
    
    public Header getRequiredHeader() {
        return requirements_.hasHeaderRequirement() ? requirements_.getRequiredHeader() : null;
    }
    
    public boolean tierEquals(String tierType, int tierRank) {
        return tierType_.equals(tierType) && tierRank == tierRank_;
    }
    
    public boolean tierRankEquals(int tierRank) {
        return tierRank == tierRank_;
    }
    
    public boolean meetsSkillRequirements(PlayerCharacter character) {
        return requirements_.meetsRequirements(character, this);
    }
    
    public Requirements getRequirements() {
        return requirements_;
    }

}
