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
package mage.sets.betrayersofkamigawa;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.DelayedTriggeredAbility;
import mage.abilities.common.delayed.AtEndOfTurnDelayedTriggeredAbility;
import mage.abilities.effects.ContinuousEffect;
import mage.abilities.effects.Effect;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.ExileTargetEffect;
import mage.abilities.effects.common.continious.GainAbilityTargetEffect;
import mage.abilities.keyword.HasteAbility;
import mage.abilities.keyword.SpliceOntoArcaneAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.filter.FilterCard;
import mage.filter.predicate.mageobject.SupertypePredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.common.TargetCardInYourGraveyard;
import mage.target.targetpointer.FixedTarget;

/**
 *
 * @author LevelX2
 */
public class GoryosVengeance extends CardImpl {

    private static final FilterCard filter = new FilterCard("legendary creature card");

    static {
        filter.add(new SupertypePredicate("Legendary"));
    }

    public GoryosVengeance(UUID ownerId) {
        super(ownerId, 67, "Goryo's Vengeance", Rarity.RARE, new CardType[]{CardType.INSTANT}, "{1}{B}");
        this.expansionSetCode = "BOK";
        this.subtype.add("Arcane");

        this.color.setBlack(true);

        // Return target legendary creature card from your graveyard to the battlefield. That creature gains haste. Exile it at the beginning of the next end step.
        this.getSpellAbility().addEffect(new GoryosVengeanceEffect());
        this.getSpellAbility().addTarget(new TargetCardInYourGraveyard(filter));

        // Splice onto Arcane {2}{B}
        this.addAbility(new SpliceOntoArcaneAbility("{2}{B}"));
    }

    public GoryosVengeance(final GoryosVengeance card) {
        super(card);
    }

    @Override
    public GoryosVengeance copy() {
        return new GoryosVengeance(this);
    }
}

class GoryosVengeanceEffect extends OneShotEffect {

    public GoryosVengeanceEffect() {
        super(Outcome.PutCardInPlay);
        this.staticText = "Return target legendary creature card from your graveyard to the battlefield. That creature gains haste. Exile it at the beginning of the next end step";
    }

    public GoryosVengeanceEffect(final GoryosVengeanceEffect effect) {
        super(effect);
    }

    @Override
    public GoryosVengeanceEffect copy() {
        return new GoryosVengeanceEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null) {
            Card card = game.getCard(targetPointer.getFirst(game, source));
            if (card != null) {
                if (controller.putOntoBattlefieldWithInfo(card, game, Zone.GRAVEYARD, source.getId())) {
                    Permanent permanent = game.getPermanent(card.getId());
                    if (permanent != null) {
                        // Haste
                        ContinuousEffect effect = new GainAbilityTargetEffect(HasteAbility.getInstance(), Duration.Custom);
                        effect.setTargetPointer(new FixedTarget(permanent.getId()));
                        game.addEffect(effect, source);
                        // Exile it at end of turn
                        Effect exileEffect = new ExileTargetEffect(new StringBuilder("Exile ").append(permanent.getName()).append(" at the beginning of the next end step").toString());
                        exileEffect.setTargetPointer(new FixedTarget(card.getId()));
                        DelayedTriggeredAbility delayedAbility = new AtEndOfTurnDelayedTriggeredAbility(exileEffect);
                        delayedAbility.setSourceId(source.getSourceId());
                        delayedAbility.setControllerId(source.getControllerId());
                        game.addDelayedTriggeredAbility(delayedAbility);
                        return true;

                    }
                }
            }
        }
        return false;
    }
}
