package mapstuff;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;

public class FakeMapView implements Serializable{
        private static final long serialVersionUID = 2801184242956431291L;
        public FakeMapView(World world){
                view=Bukkit.createMap(world);
                id=view.getId();
        }
        public short getId() {
                return id;
        }
        public MapView getView(){
                if(view==null){
                        view=Bukkit.getMap(id);
                }
                return view;
        }
        public int getCenterX(){
                return getView().getCenterX();
        }
        public int getCenterZ(){
                return getView().getCenterZ();
        }
        private short id;
        private transient MapView view;
        public void setScale(Scale distance) {
                getView().setScale(distance);
        }
        public void setCenterX(int i) {
                getView().setCenterX(i);
        }
        public void setCenterZ(int j) {
                getView().setCenterZ(j);
        }
}

