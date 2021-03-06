package cubex2.cs4.plugins.vanilla;

import com.google.gson.Gson;
import cubex2.cs4.TestContentHelper;
import cubex2.cs4.TestUtil;
import cubex2.cs4.api.BlankContent;
import cubex2.cs4.api.InitPhase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FuelTests
{
    private static Gson gson;

    @BeforeClass
    public static void setup()
    {
        gson = TestUtil.createGsonBuilder().create();
    }

    @Test
    public void testDeserialization()
    {
        Fuel fuel = gson.fromJson("{ \"item\": \"minecraft:apple\", \"burnTime\":1337 }", Fuel.class);
        fuel.init(InitPhase.PRE_INIT, new TestContentHelper("{}", TestContent.class));

        assertEquals(1337, fuel.burnTime);
        assertEquals(1337, fuel.getBurnTime(new ItemStack(Items.APPLE)));
        assertEquals(0, fuel.getBurnTime(new ItemStack(Items.BOW)));
    }

    @Test
    public void testBurnTime()
    {
        Fuel fuel = gson.fromJson("{ \"item\": \"minecraft:bow\", \"burnTime\":1337 }", Fuel.class);
        fuel.init(InitPhase.PRE_INIT, new TestContentHelper("{}", TestContent.class));

        assertEquals(1337, fuel.getBurnTime(new ItemStack(Items.BOW)));
        assertEquals(0, fuel.getBurnTime(new ItemStack(Items.BOW, 64, 1)));
    }

    @Test
    public void testBurnTimeWithWildcard()
    {
        Fuel fuel = gson.fromJson("{ \"item\": \"minecraft:bow@all\", \"burnTime\":1337 }", Fuel.class);
        fuel.init(InitPhase.PRE_INIT, new TestContentHelper("{}", TestContent.class));

        assertEquals(1337, fuel.getBurnTime(new ItemStack(Items.BOW)));
        assertEquals(1337, fuel.getBurnTime(new ItemStack(Items.BOW, 64, 1)));
    }

    @Test
    public void testBurnTimeWithNbt()
    {
        Fuel fuel = gson.fromJson("{ \"item\": {\"item\" : \"minecraft:bow\", \"nbt\":\"{AInt:1}\" }, \"burnTime\":1337 }", Fuel.class);
        fuel.init(InitPhase.PRE_INIT, new TestContentHelper("{}", TestContent.class));

        ItemStack stack = new ItemStack(Items.BOW);
        stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger("AInt", 1);

        assertEquals(0, fuel.getBurnTime(new ItemStack(Items.BOW)));
        assertEquals(1337, fuel.getBurnTime(stack));
    }

    @Test
    public void testBurnTimeWithWrongNbt()
    {
        Fuel fuel = gson.fromJson("{ \"item\": {\"item\" : \"minecraft:bow\", \"nbt\":\"{AInt:1}\" }, \"burnTime\":1337 }", Fuel.class);
        fuel.init(InitPhase.PRE_INIT, new TestContentHelper("{}", TestContent.class));

        ItemStack stack = new ItemStack(Items.BOW);
        stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger("AInt", 2);

        assertEquals(0, fuel.getBurnTime(stack));
    }

    private static class TestContent extends BlankContent
    {

    }
}
