package org.motechproject.whp.common.repository;


import org.ektorp.support.GenericRepository;
import org.motechproject.whp.common.domain.District;

public interface AllDistricts extends GenericRepository<District>{
    public void refresh();
}