package edu.upc.eseiaat.pma.balletbo.isaac.shoppinglist3;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ShoppingListActivity extends AppCompatActivity {

    //Fitxer on es guardarà la llista de la compra
    private static final String FILENAME = "shopping_list.txt";
    private static final int MAX_BYTES = 8000;

    private ArrayList<Shoppingitem> itemList;
    private ShoppingListAdapter adapter;

    private ListView list;
    private Button btn_add;
    private EditText edit_item;

    private void WriteItemList(){

        try {
            //Per crear el fitxer de la llista
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            //Passem per tots els ítems de la llista
            for(int i = 0; i < itemList.size(); i++){
                Shoppingitem it = itemList.get(i);
                /*Format de la llista que crearem tipo:
                    Patatas;false
                    Papel WC;true
                    1;true
                    2;false
                    3;false

                    %s -Per escriure un String
                    %b -Per escriure un boolean
                    \n per saltar de linia
                */
                String line = String.format("%s;%b\n", it.getText(), it.isChecked());
                //Grabem la linia al fitxer
                fos.write(line.getBytes());
            }
            //Tanquem el fitxer
            fos.close();
        //Excepció si no troba el fitxer
        } catch (FileNotFoundException e) {
            //Log.e es missatge d'error i surt en vermell
            Log.e("isaac", "writeItemList: FileNotFoundException");
            Toast.makeText(this, R.string.cannot_write, Toast.LENGTH_SHORT).show();
        //Excepció si no hi ha suficient espai a la memòria
        } catch (IOException e) {
            Log.e("isaac", "writeItemList: IOException");
            Toast.makeText(this, R.string.cannot_write, Toast.LENGTH_SHORT).show();
        }
    }

    //Fem el contrari de WriteItemList, en aquest cas, per llegir fitxer
    private void readItemList(){
        itemList = new ArrayList<>();
        try {
            //Per obrir el fitxer de la llista
            FileInputStream fis = openFileInput(FILENAME);
            //Creem una taula de Bytes per establir el tamany del fitxer
            byte[] buffer = new byte[MAX_BYTES];
            int nread = fis.read(buffer);
            if(nread > 0) {
                //String del tamany de tot el fitxer
                String content = new String(buffer, 0, nread);
                //Partim cada cop que hi hagi un canvi de linia (\n)
                String[] lines = content.split("\n");
                /* Un cop tenim les linies, llegim el contingut de cadascuna
                diferencian la part d'abans i despres del ; i afegim a la llista
                si la segons part és igual a true */
                for (String line : lines) {
                    String[] parts = line.split(";");
                    itemList.add(new Shoppingitem(parts[0], parts[1].equals("true")));
                }
            }
            fis.close();
        //Excepció si no troba el fitxer
        } catch (FileNotFoundException e) {
            //Log.i es missatge d'informació
            Log.i("isaac", "readItemList: FileNotFoundException");
        } catch (IOException e) {
            Log.e("isaac", "readItemList: IOException");
            Toast.makeText(this, R.string.cannot_read, Toast.LENGTH_SHORT).show();
        }
    }

    //Quan es para o es surt de la app
    @Override
    protected void onStop() {
        super.onStop();
        WriteItemList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        list = (ListView) findViewById(R.id.list);
        btn_add = (Button) findViewById(R.id.btn_add);
        edit_item = (EditText) findViewById(R.id.edit_item);

        //Creem una llista d'Array de la funció creada anteriorment
        readItemList();

        //Creem l'adaptador del ArrayList (que coneix AndroidStudio)
        adapter = new ShoppingListAdapter(
                this,
                R.layout.shopping_item,
                itemList
        );

        //Funcio al clickar al btn_add
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        edit_item.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                addItem();
                return true;
            }
        });


        list.setAdapter(adapter);

        //Per enterarnos quan han clickat un element
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                //Amb aquest mètode, si l'element està marcat,
                //es desmarcarà i si no ho estava, es marcarà
                itemList.get(pos).toggleChecked();
                //Quan es fa un canvi en l'adaptador, aquest s'avisa perque s'actualitzi
                adapter.notifyDataSetChanged();
            }
        });

        //Metode per quan apreten el boto durant un rato
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> list, View item, int pos, long id) {
                maybeRemoveItem(pos);
                return true;
            }
        });
    }

    private void maybeRemoveItem(final int pos) {
        //Creem un quadre de diàleg per confirmar si volem realitzar l'acció
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm);
        String fmt = getResources().getString(R.string.confirm_message);
        //Missatge del quadre de dialeg i el ítem que es vol eliminar
        builder.setMessage(String.format(fmt, itemList.get(pos).getText()));
        builder.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Eliminem el text concret (pos) de la llista
                itemList.remove(pos);
                //Quan es fa un canvi en el llista d'Array, s'avisa a l'adaptador
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(android.R.string.cancel,null);
        builder.create().show();


    }

    private void addItem() {
        //Agafem el text de la caixeta
        String item_text = edit_item.getText().toString();
        //Si el text no esta en blanc
        if(!item_text.isEmpty()) {
            //Afegim a la llista el text agafat
            itemList.add(new Shoppingitem(item_text));
            //Quan es fa un canvi en el llista d'Array, s'avisa a l'adaptador
            adapter.notifyDataSetChanged();
            edit_item.setText("");
            //La lista se mueva sola hasta el elemento que acabo de poner para que yo lo pueda ver
            list.smoothScrollToPosition(itemList.size()-1);
        }
    }
    //Mètode per reomplir el menu i visualitzar-lo
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    //Mètode per què es pugui seleccionar la opció del menu
    public boolean onOptionsItemSelected(MenuItem item){
        //Handle item selection
        switch (item.getItemId()){
            case R.id.clear_checked:
                clearChecked();
                return true;

            case R.id.clear_all:
                clearAll();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Mètode per netejar tota la pantalla
    private void clearAll() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm);
        builder.setMessage(R.string.confirm_clear_all);
        builder.setPositiveButton(R.string.clear_all, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemList.clear();
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    //Mètode per netejar de pantalla els ítems que tinguin el check
    private void clearChecked() {
        int i = 0;
        while (i < itemList.size()){
            if(itemList.get(i).isChecked()){
                itemList.remove(i);
            }else{
                i++;
            }
        }
        adapter.notifyDataSetChanged();
    }
}
