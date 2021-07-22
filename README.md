# Crear usuario

Paciente paciente1 =new Paciente("paciente","contrase�a");
//registro paciente  
ucontainer.register(paciente1);
//Persistencia
ucontainer.persistance();

# Login de usuarios
//verificacion con hash en BCrypt
if(ucontainer.login("paciente", "contrase�a") instanceof Usuario)
{
	//LOGIN
}

#Pantalla segun el tipo de usuario