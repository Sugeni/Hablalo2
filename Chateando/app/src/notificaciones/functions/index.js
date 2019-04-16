/*
this is the node.js that should be deployed to Firebase-Functions
the code being presented here is working fine on the day August 06,2018. */   
 'use strict' 

    const functions = require('firebase-functions'); 
    const admin = require('firebase-admin'); 
    admin.initializeApp(functions.config().firebase); 
 
    exports.sendNotification = functions.database.ref('/Notificaciones/{user_id}/{notification_id}').onWrite((data, context) => { 

    	const user_id = context.params.user_id; 
    	const notification_id = context.params.notification_id; 
    	console.log('Paso 1, enviar notificacion  a : ', user_id); 
    

    	const from_user = admin.database().ref(`/Notificaciones/${user_id}/${notification_id}`).once('value');

   		return from_user.then(fromUserResult => {
    		
    		const from_user_id = fromUserResult.val().from;
    		console.log('Paso 2, determinar emisor: ', from_user_id ); 

    		const userQueryNombre = admin.database().ref(`/Usuarios/${from_user_id}/nombre`).once('value');
            const deviceToken = admin.database().ref(`/Usuarios/${user_id}/token_dispositivo`).once('value');

            //Se le pasan las consultas a utilizar
            return Promise.all([userQueryNombre, deviceToken]).then( result =>{

                                const userName = result[0].val();
                                const token_id = result[1].val();

                                console.log('Paso 3, nombre del emisor: ', userName); 

                                const payload={
                                     notification:{
                                        title: "Solicitud de amistad",
                                        body: `${userName} te ha enviado una solicitud`,
                                        icon:"logo.png",
                                        click_action: "isware.uneg.es.chateando_TARGET_NOTIFICATION"
                                     },
                        
                                    data:{
                                        from_user_id: from_user_id
                                    }
                                };

                    return admin.messaging().sendToDevice(token_id,payload).then(response =>{
                            console.log('Paso 4: la notificacion fue enviada');
                            return true;
                        });


        });

    }); 
});