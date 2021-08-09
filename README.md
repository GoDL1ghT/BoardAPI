# Простая АПИ для создания своих картин в рамках

## Добавление в проект
```gradle
repositories {
    //Other repos
    maven { url 'https://jitpack.io' }
}

dependencies {
    //Other depends
    implementation 'com.github.asyncdargen:BoardAPI:master-SNAPSHOT'
}
```

**Пример: `TestPlugin.java`**
```java
import net.minecraft.server.v1_12_R1.EnumDirection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.dargen.board.BannerBoard;
import ru.dargen.board.BoardAPI;
import ru.dargen.board.BoardRenderer;

public class TestPlugin extends JavaPlugin {

    private BannerBoard board;

    public void onEnable() {
        getCommand("test").setExecutor(this);
        
        BoardAPI.init(this);

        Location location = new Location(Bukkit.getWorlds().get(0), 100, 70, 100);

        //Создание публичного борда
        board = BoardAPI.createBanner(location, EnumDirection.WEST, 3, 3);

        //Отрисовка текста
        BoardRenderer renderer = board.getRenderer();
        renderer.drawCenteredString("§bHello World!", board.getRenderer().getHeight() / 2);
        renderer.getGraphics().dispose();

        //Обновление борда
        board.updateFragments();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;

        Player player = (Player) sender;

        Location location = player.getEyeLocation().add(0, 2, 0);

        //Создание приватного борда для игрока прописавшего команду
        BannerBoard board = BoardAPI.createPrivateBanner(location, EnumDirection.WEST, 3, 3, player);

        //Отрисовка текста
        BoardRenderer renderer = board.getRenderer();
        renderer.drawCenteredString("§bHello §a" + player.getName(), board.getRenderer().getHeight() / 2);
        renderer.getGraphics().dispose();

        //Обновление борда
        board.updateFragments();

        return true;
    }

}

```
