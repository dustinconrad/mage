/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package mage.sets.theros;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.effects.common.DamageTargetEffect;
import mage.abilities.keyword.HeroicAbility;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Rarity;
import mage.target.common.TargetCreatureOrPlayer;

/**
 *
 * @author LevelX2
 */
public class LabyrinthChampion extends CardImpl<LabyrinthChampion> {

    public LabyrinthChampion(UUID ownerId) {
        super(ownerId, 126, "Labyrinth Champion", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{3}{R}");
        this.expansionSetCode = "THS";
        this.subtype.add("Human");
        this.subtype.add("Warrior");

        this.color.setRed(true);
        this.power = new MageInt(2);
        this.toughness = new MageInt(2);

        // Heroic - Whenever you cast a spell that targets Labyrinth Champion, Labyrinth Champion deals 2 damage to target creature or player.
        Ability ability = new HeroicAbility(new DamageTargetEffect(2), false);
        ability.addTarget(new TargetCreatureOrPlayer(true));
        this.addAbility(ability);
    }

    public LabyrinthChampion(final LabyrinthChampion card) {
        super(card);
    }

    @Override
    public LabyrinthChampion copy() {
        return new LabyrinthChampion(this);
    }
}