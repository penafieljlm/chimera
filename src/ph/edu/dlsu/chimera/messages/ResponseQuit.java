/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.messages;

import ph.edu.dlsu.chimera.core.ReturnParameter;

/**
 *
 * @author User
 */
public class ResponseQuit implements Response {

    @Override
    public Command handleResponse(ReturnParameter returned) {
        return new MessageFinished();
    }

}
