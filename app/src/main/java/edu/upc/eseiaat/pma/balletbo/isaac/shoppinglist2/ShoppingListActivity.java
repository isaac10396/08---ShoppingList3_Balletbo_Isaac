package edu.upc.eseiaat.pma.balletbo.isaac.shoppinglist2;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ShoppingListActivity extends AppCompatActivity {

    private ArrayList<Shoppingitem> itemList;
    private ShoppingListAdapter adapter;

    private ListView list;
    private Button btn_add;
    private EditText edit_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        list = (ListView) findViewById(R.id.list);
        btn_add = (Button) findViewById(R.id.btn_add);
        edit_item = (EditText) findViewById(R.id.edit_item);

        //Creem una llista d'Array
        itemList = new ArrayList<>();
        itemList.add(new Shoppingitem("Patatas"));
        itemList.add(new Shoppingitem("Papel WC"));
        itemList.add(new Shoppingitem("Zanahorias"));
        itemList.add(new Shoppingitem("Copas Danone"));

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
}
