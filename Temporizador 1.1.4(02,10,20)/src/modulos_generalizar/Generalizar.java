package modulos_generalizar;

import java.util.HashMap;
import java.util.Iterator;
import java.util.HashSet;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.text.DecimalFormat;



public class Generalizar {
    
    private HashMap<String,String> niveles_g = new HashMap<String, String>();
    
    private HashSet<String> acciones_g;
    
    private String nTabla_gen;
    
        
     public Generalizar(String nivel_g) throws SQLException{
       
       niveles_g.put("Alto","generaliza3");
       niveles_g.put("Medio","generaliza2");
       niveles_g.put("Bajo","generaliza1");
         
       nTabla_gen = niveles_g.get(nivel_g);
        
       acciones_g = getAcciones("Accion_general",null);
     }
     
    //Retorna pares de clave-valor(nombre-horas)
    public HashMap<String, Float> getDatosGrafico() throws SQLException{
       
      HashMap<String, Float> datosT = new HashMap<>();//Perdida de minutos --> Mejorar
      HashSet<String> acciones_e;
      
      Iterator<String> i = acciones_g.iterator();
      
      try(Connection cn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bd_horarios","root","")){   
            
        while(i.hasNext()){
      
          String nombreG = i.next();  
          Statement st = cn.createStatement();
         
          String consulta = "SELECT (SUM(HORAS) + SUM(MINUTOS)/60 + SUM(SEGUNDOS)/3600) AS " + nombreG + " FROM datostiempo " + "WHERE ";
        
          acciones_e = getAcciones("Accion_especifica", nombreG);//º
        
          Iterator<String> i2 = acciones_e.iterator();
        
         
          while(i2.hasNext()){
         
            String nomE = i2.next();
            
           //º
            if(i2.hasNext()){ 
             consulta += "NOMBRE = '" + nomE + "' OR "; 
             }else{
               consulta += "NOMBRE = '" + nomE + "';";
             }
          }
         
          ResultSet rs = st.executeQuery(consulta);
          rs.next();
         
          DecimalFormat df = new DecimalFormat("#.00");
          String strD = df.format(rs.getDouble(1)).replace(",",".");
          float valorD = Float.parseFloat(strD);
          datosT.put(nombreG, valorD);
         
        }
      
     }
        return datosT;
    }
    

   public String getNombreTabla(){
     return nTabla_gen;
   }
     

   //Revisar para optimizar la obtencion de datos
   public HashSet<String> getAcciones(String tipo_accion, String accionG) throws SQLException{
      
     HashSet<String> nombres = new HashSet<String>();
     ResultSet rs;
     
     try(Connection cn = DriverManager.getConnection("jdbc:mysql://localhost/bd_horarios","root","")){
     
      if(tipo_accion.contains("general")){
           
           
       Statement st = cn.createStatement();
       rs = st.executeQuery("SELECT DISTINCT " + tipo_accion + " FROM " + nTabla_gen);
    
       }else{//accion especifica
        
         PreparedStatement pst = cn.prepareStatement("SELECT " + tipo_accion + " FROM " + nTabla_gen + " WHERE Accion_general = ?");
         pst.setString(1, accionG);
         rs = pst.executeQuery();
       }
    
       while(rs.next()){
         nombres.add(rs.getString(1));
       }
       
      }
     
     return nombres;
     
   }

   
}
