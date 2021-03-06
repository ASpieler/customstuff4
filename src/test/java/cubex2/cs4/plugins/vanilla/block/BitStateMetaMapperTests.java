package cubex2.cs4.plugins.vanilla.block;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Bootstrap;
import net.minecraft.util.EnumFacing;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BitStateMetaMapperTests
{
    private static final PropertyEnum<EnumSubtype> SUBTYPE = PropertyEnum.create("subtype", EnumSubtype.class);
    private static final PropertyDirection DIRECTION = PropertyDirection.create("facing");
    private static final PropertyDirection DIRECTION2 = PropertyDirection.create("facing2", ImmutableList.of(EnumFacing.NORTH, EnumFacing.SOUTH));

    private static Block BLOCK;

    @BeforeClass
    public static void setup()
    {
        Bootstrap.register();

        BLOCK = new TestBlock();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_tooManyBits_shouldThrow()
    {
        BitStateMetaMapper mapper = new BitStateMetaMapper<>(SUBTYPE, DIRECTION);
    }

    @Test
    public void test_getMetaFromState()
    {
        BitStateMetaMapper mapper = new BitStateMetaMapper<>(DIRECTION, DIRECTION2);

        IBlockState state = BLOCK.getDefaultState()
                                 .withProperty(DIRECTION, EnumFacing.UP)
                                 .withProperty(DIRECTION2, EnumFacing.SOUTH);

        int meta = mapper.getMetaFromState(state);

        assertEquals(1 | (1 << 3), meta);
    }

    @Test
    public void test_getStateFromMeta()
    {
        BitStateMetaMapper<Block> mapper = new BitStateMetaMapper<>(DIRECTION, DIRECTION2);

        IBlockState state = mapper.getStateFromMeta(BLOCK, 1 | (1 << 3));

        assertEquals(EnumFacing.UP, state.getValue(DIRECTION));
        assertEquals(EnumFacing.SOUTH, state.getValue(DIRECTION2));
    }

    @Test
    public void test_getBitCount()
    {
        assertEquals(0, BitStateMetaMapper.getBitCount(0));
        assertEquals(1, BitStateMetaMapper.getBitCount(1));
        assertEquals(1, BitStateMetaMapper.getBitCount(2));
        assertEquals(2, BitStateMetaMapper.getBitCount(3));
        assertEquals(4, BitStateMetaMapper.getBitCount(13));
        assertEquals(4, BitStateMetaMapper.getBitCount(15));
        assertEquals(4, BitStateMetaMapper.getBitCount(16));
    }

    private static class TestBlock extends Block
    {
        TestBlock()
        {
            super(Material.ANVIL);
        }

        @Override
        protected BlockStateContainer createBlockState()
        {
            return new BlockStateContainer(this, DIRECTION, DIRECTION2);
        }
    }
}
