package mage.abilities.effects.common;

import mage.abilities.Ability;
import mage.abilities.effects.ContinuousRuleModifiyingEffectImpl;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.PhaseStep;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;

/**
 * @author nantuko
 */
public class SkipEnchantedUntapEffect extends ContinuousRuleModifiyingEffectImpl {

    public SkipEnchantedUntapEffect() {
        super(Duration.WhileOnBattlefield, Outcome.Detriment, false, true);
        staticText = "Enchanted permanent doesn't untap during its controller's untap step";
    }

    public SkipEnchantedUntapEffect(final SkipEnchantedUntapEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        return true;
    }

    @Override
    public SkipEnchantedUntapEffect copy() {
        return new SkipEnchantedUntapEffect(this);
    }

    @Override
    public String getInfoMessage(Ability source, GameEvent event, Game game) {
        Permanent enchantment = game.getPermanent(source.getSourceId());
        if (enchantment != null && enchantment.getAttachedTo() != null) {
            Permanent enchanted = game.getPermanent(enchantment.getAttachedTo());
            if (enchanted != null) {
                return enchanted.getLogName() + " doesn't untap during its controller's untap step (" + enchantment.getLogName() + ")";
            }            
        }
        return null;
    }

    
    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        if (game.getTurn().getStepType() == PhaseStep.UNTAP && event.getType() == GameEvent.EventType.UNTAP) {
            Permanent enchantment = game.getPermanent(source.getSourceId());
            if (enchantment != null && enchantment.getAttachedTo() != null) {
                Permanent permanent = game.getPermanent(enchantment.getAttachedTo());
                if (permanent != null && event.getTargetId().equals(permanent.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

}
