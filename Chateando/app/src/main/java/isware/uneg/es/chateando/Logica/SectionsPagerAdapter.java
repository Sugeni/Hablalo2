package isware.uneg.es.chateando.Logica;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import isware.uneg.es.chateando.Interfaz.AmigosFragment;
import isware.uneg.es.chateando.Interfaz.ChatsFragment;
import isware.uneg.es.chateando.Interfaz.SolicitudesFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter{
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                SolicitudesFragment solicitudesFragment = new SolicitudesFragment();
                return solicitudesFragment;
            case 1:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 2:
                 AmigosFragment amigosFragment = new AmigosFragment();
                 return amigosFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle( int position){

        switch (position){
            case 0:
                return "Solicitudes";
            case 1:
                 return "Chats";
            case 2:
                 return "Amigos";

            default:
                return null;
        }

    }
}
