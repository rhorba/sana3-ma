import { ArtisanProfileActions } from './artisan-profile.actions';
import { artisanProfileReducer } from './artisan-profile.reducer';
import { ArtisanProfileState, initialArtisanProfileState } from './artisan-profile.state';

describe('artisanProfileReducer', () => {
  const response = {
    id: 'profile-1',
    displayName: 'Yassine Zellige',
    craftType: 'Zellige tiling',
    region: 'Fes',
    bio: 'Third-generation artisan.',
    contactPhone: '+212600000000',
    createdAt: '2026-01-01T00:00:00Z',
    updatedAt: '2026-01-02T00:00:00Z',
  };

  it('returns the initial state for an unknown action', () => {
    expect(artisanProfileReducer(undefined, { type: '@@INIT' })).toEqual(initialArtisanProfileState);
  });

  it('sets loading and clears error on loadProfile', () => {
    const withError: ArtisanProfileState = { ...initialArtisanProfileState, error: 'previous error' };
    const state = artisanProfileReducer(withError, ArtisanProfileActions.loadProfile());
    expect(state.loading).toBe(true);
    expect(state.error).toBeNull();
  });

  it('populates fields and marks loaded on loadProfileSuccess/saveProfileSuccess', () => {
    const state = artisanProfileReducer(initialArtisanProfileState, ArtisanProfileActions.loadProfileSuccess({ response }));
    expect(state).toEqual({
      id: response.id,
      displayName: response.displayName,
      craftType: response.craftType,
      region: response.region,
      bio: response.bio,
      contactPhone: response.contactPhone,
      loading: false,
      loaded: true,
      saving: false,
      error: null,
    });
  });

  it('marks loaded without populating fields on loadProfileNotFound (empty state)', () => {
    const loading: ArtisanProfileState = { ...initialArtisanProfileState, loading: true };
    const state = artisanProfileReducer(loading, ArtisanProfileActions.loadProfileNotFound());
    expect(state.loaded).toBe(true);
    expect(state.loading).toBe(false);
    expect(state.displayName).toBeNull();
    expect(state.error).toBeNull();
  });

  it('sets an error and leaves loaded false on loadProfileFailure (genuine error, not empty state)', () => {
    const loading: ArtisanProfileState = { ...initialArtisanProfileState, loading: true };
    const state = artisanProfileReducer(loading, ArtisanProfileActions.loadProfileFailure({ message: 'network error' }));
    expect(state.loading).toBe(false);
    expect(state.loaded).toBe(false);
    expect(state.error).toBe('network error');
  });

  it('sets saving and clears error on saveProfile', () => {
    const withError: ArtisanProfileState = { ...initialArtisanProfileState, error: 'previous error' };
    const state = artisanProfileReducer(
      withError,
      ArtisanProfileActions.saveProfile({
        displayName: 'a',
        craftType: 'b',
        region: 'c',
        bio: 'd',
        contactPhone: 'e',
      }),
    );
    expect(state.saving).toBe(true);
    expect(state.error).toBeNull();
  });

  it('sets an error and clears saving on saveProfileFailure', () => {
    const saving: ArtisanProfileState = { ...initialArtisanProfileState, saving: true };
    const state = artisanProfileReducer(saving, ArtisanProfileActions.saveProfileFailure({ message: "Couldn't save, try again." }));
    expect(state.saving).toBe(false);
    expect(state.error).toBe("Couldn't save, try again.");
  });
});
