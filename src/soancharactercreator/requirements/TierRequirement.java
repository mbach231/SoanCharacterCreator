/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soancharactercreator.requirements;

import java.util.Set;
import soancharactercreator.Header;
import soancharactercreator.PlayerCharacter;
import soancharactercreator.Skill;

/**
 *
 *
 */
public class TierRequirement extends Requirement {

    private final Header header_;
    private final String tierType_;
    private final Integer tierRank_;
    private final int numRequired_;

    public TierRequirement(Header header, String tierType, Integer tierRank, int numRequired) {
        header_ = header;
        tierType_ = tierType;
        tierRank_ = tierRank;
        numRequired_ = numRequired;
    }

    @Override
    public boolean meetsRequirements(PlayerCharacter character, Skill skill) {

        Set<Skill> headerSkillSet = character.getKnownHeaderSkills(header_);

        if (headerSkillSet == null) {
            return numRequired_ == 0;
        }

        int numKnownOfTier = 0;

        if (tierType_ == null && tierRank_ == null) {

            for (Skill headerSkill : headerSkillSet) {
                if (!headerSkill.equals(skill)) {

                    Requirements requirements = headerSkill.getRequirements();

                    if (requirements.hasSkillRequirement()) {
                        if (!requirements.getSkillRequirement().isStrictlyRequiredSkill(skill)) {
                            numKnownOfTier++;
                        }
                    } else {
                        numKnownOfTier++;
                    }

                }
            }

        } 
        else if (tierType_ == null) {
          for (Skill headerSkill : headerSkillSet) {
                if (headerSkill.tierRankEquals(tierRank_)) {
                    numKnownOfTier++;
                }
            }
        } else {

            for (Skill headerSkill : headerSkillSet) {
                if (headerSkill.tierEquals(tierType_, tierRank_)) {
                    numKnownOfTier++;
                }
            }
        }

        return numKnownOfTier >= numRequired_;
    }

}
