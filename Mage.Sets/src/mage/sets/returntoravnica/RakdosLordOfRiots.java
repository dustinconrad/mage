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
package mage.sets.returntoravnica;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.SpellAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.dynamicvalue.common.OpponentsLostLifeCount;
import mage.abilities.effects.ContinuousRuleModifiyingEffectImpl;
import mage.abilities.effects.common.cost.CostModificationEffectImpl;
import mage.abilities.keyword.FlashbackAbility;
import mage.abilities.keyword.FlyingAbility;
import mage.abilities.keyword.RetraceAbility;
import mage.abilities.keyword.TrampleAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.CostModificationType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.GameEvent.EventType;
import mage.util.CardUtil;

/**
 *
 * @author LevelX2
 */
public class RakdosLordOfRiots extends CardImpl {

    public RakdosLordOfRiots(UUID ownerId) {
        super(ownerId, 187, "Rakdos, Lord of Riots", Rarity.MYTHIC, new CardType[]{CardType.CREATURE}, "{B}{B}{R}{R}");
        this.expansionSetCode = "RTR";
        this.supertype.add("Legendary");
        this.subtype.add("Demon");
        this.color.setBlack(true);
        this.color.setRed(true);
        this.power = new MageInt(6);
        this.toughness = new MageInt(6);

        // You can't cast Rakdos, Lord of Riots unless an opponent lost life this turn.
        this.addAbility(new SimpleStaticAbility(Zone.ALL, new RakdosLordOfRiotsCantCastEffect()));

        // Flying, trample
        this.addAbility(FlyingAbility.getInstance());
        this.addAbility(TrampleAbility.getInstance());

        // Creature spells you cast cost {1} less to cast for each 1 life your opponents have lost this turn.
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new RakdosLordOfRiotsCostReductionEffect()));
    }

    public RakdosLordOfRiots(final RakdosLordOfRiots card) {
        super(card);
    }

    @Override
    public RakdosLordOfRiots copy() {
        return new RakdosLordOfRiots(this);
    }
}

class RakdosLordOfRiotsCantCastEffect extends ContinuousRuleModifiyingEffectImpl {

    public RakdosLordOfRiotsCantCastEffect() {
        super(Duration.WhileOnBattlefield, Outcome.Detriment);
        staticText = "You can't cast {this} unless an opponent lost life this turn";
    }

    public RakdosLordOfRiotsCantCastEffect(final RakdosLordOfRiotsCantCastEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        return true;
    }

    @Override
    public RakdosLordOfRiotsCantCastEffect copy() {
        return new RakdosLordOfRiotsCantCastEffect(this);
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        if (event.getType() == EventType.CAST_SPELL && event.getSourceId().equals(source.getSourceId())) {
            if (new OpponentsLostLifeCount().calculate(game, source) == 0) {
                return true;
            }
        }
        return false;
    }
}

class RakdosLordOfRiotsCostReductionEffect extends CostModificationEffectImpl {

    RakdosLordOfRiotsCostReductionEffect() {
        super(Duration.WhileOnBattlefield, Outcome.Benefit, CostModificationType.REDUCE_COST);
        staticText = "Creature spells you cast cost {1} less to cast for each 1 life your opponents have lost this turn";
    }

    RakdosLordOfRiotsCostReductionEffect(RakdosLordOfRiotsCostReductionEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source, Ability abilityToModify) {
        Ability spellAbility = (SpellAbility) abilityToModify;
        if (spellAbility != null) {
            OpponentsLostLifeCount dynamicValue = new OpponentsLostLifeCount();
            int amount = dynamicValue.calculate(game, source);
            if (amount > 0) {
                CardUtil.reduceCost(spellAbility, amount);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean applies(Ability abilityToModify, Ability source, Game game) {
        if (abilityToModify instanceof SpellAbility || abilityToModify instanceof FlashbackAbility || abilityToModify instanceof RetraceAbility) {
            Card sourceCard = game.getCard(abilityToModify.getSourceId());
            if (sourceCard != null && abilityToModify.getControllerId().equals(source.getControllerId()) && (sourceCard.getCardType().contains(CardType.CREATURE))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public RakdosLordOfRiotsCostReductionEffect copy() {
        return new RakdosLordOfRiotsCostReductionEffect(this);
    }

}
