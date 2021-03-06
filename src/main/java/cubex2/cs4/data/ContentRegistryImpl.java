package cubex2.cs4.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializer;
import cubex2.cs4.api.Content;
import cubex2.cs4.api.ContentRegistry;
import cubex2.cs4.api.LoaderPredicate;
import cubex2.cs4.api.TileEntityModuleSupplier;
import cubex2.cs4.plugins.vanilla.TileEntityModuleRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public class ContentRegistryImpl implements ContentRegistry, DeserializationRegistry, LoaderPredicateRegistry, TileEntityModuleRegistry
{
    private final Map<String, Class<? extends Content>> types = Maps.newHashMap();
    private final Map<String, Side> typeSides = Maps.newHashMap();
    private final Map<String, LoaderPredicate> predicates = Maps.newHashMap();
    private final List<Pair<Type, JsonDeserializer<?>>> deserializers = Lists.newArrayList();
    private final Map<String, Class<? extends TileEntityModuleSupplier>> tileEntityModules = Maps.newHashMap();

    @Override
    public <T extends Content> void registerContentType(String typeName, Class<T> clazz)
    {
        checkArgument(!types.containsKey(typeName), "Duplicate typeName: %s", typeName);

        types.put(typeName, clazz);
    }

    @Override
    public <T extends Content> void registerContentType(String typeName, Class<T> clazz, Side side)
    {
        registerContentType(typeName, clazz);

        typeSides.put(typeName, side);
    }

    @Nullable
    Class<? extends Content> getContentClass(String typeName)
    {
        if (typeSides.containsKey(typeName)
            && FMLCommonHandler.instance().getSide() != typeSides.get(typeName))
            return null;

        return types.get(typeName);
    }

    @Override
    public <T> void registerDeserializer(Type type, JsonDeserializer<T> deserializer)
    {
        deserializers.add(Pair.of(type, deserializer));
    }

    @Override
    public List<Pair<Type, JsonDeserializer<?>>> getDeserializers()
    {
        return deserializers;
    }

    @Override
    public void registerLoaderPredicate(String name, LoaderPredicate predicate)
    {
        checkArgument(!predicates.containsKey(name), "Duplicate predicate name: %s", name);

        predicates.put(name, predicate);
    }

    @Nullable
    @Override
    public LoaderPredicate getPredicate(String name)
    {
        return predicates.get(name);
    }

    @Override
    public <T extends TileEntityModuleSupplier> void registerTileEntityModule(String typeName, Class<T> clazz)
    {
        checkArgument(!tileEntityModules.containsKey("name"), "Duplicate tile entity module name: %s", typeName);

        tileEntityModules.put(typeName, clazz);
    }

    @Override
    public Class<? extends TileEntityModuleSupplier> getTileEntityModuleClass(String typeName)
    {
        return tileEntityModules.get(typeName);
    }
}
